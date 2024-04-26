package com.example.konrad.services

import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.constants.ApplicationConstants
import com.example.konrad.entity.BookingDetailsEntity
import com.example.konrad.model.*
import com.example.konrad.repositories.*
import com.example.konrad.utility.AsyncMethods
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*
import kotlin.math.min


@Service
class BookingService(
    @Autowired private val bookingRepository: BookingRepository,
    @Autowired private val bookingLocationRepository: BookingLocationRepository,
    @Autowired private val jwtTokenUtil: JwtTokenUtil,
    @Autowired private val addressDetailsRepository: AddressDetailsRepository,
    @Autowired private val doctorsDataRepository: DoctorsDataRepository,
    @Autowired private val driverDataRepository: DriverDataRepository,
    @Autowired private val patientRepository: PatientRepository,
    @Autowired private val asyncMethods: AsyncMethods,
    @Autowired private val notificationService: NotificationService
) {
    @Value("\${aggregator-user-name}")
    private lateinit var aggregatorUsername: String

    fun addNewBooking(bookingDetailsModel: BookingDetailsModel, userToken: String): ResponseEntity<*> {
        val newBookingValid = BookingDetailsConvertor.isNewBookingValid(bookingDetailsModel)
        val patientDetails = patientRepository.findById(bookingDetailsModel.patientId!!)
        if (!patientDetails.isPresent) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseModel(success = false, reason = "patient doesn't exist", body = null))
        }

        return if (newBookingValid.success == true) {
            try {
                //TODO: Add aggregator id here and send notification to aggregator
                bookingDetailsModel.aggregatorId = aggregatorUsername

                bookingDetailsModel.userId = patientDetails.get().userId
                bookingDetailsModel.bookingAmount = ApplicationConstants.BOOKING_AMOUNT

                //Add booking status
                bookingDetailsModel.bookingStatusList =
                    mutableListOf(addBookingStatus(StatusOfBooking.BookingConfirmed))

                val bookingDetailsEntity =
                    bookingRepository.save(BookingDetailsConvertor.toEntity(bookingDetailsModel, null))

                //Add frh location as initial booking location Asynchronously
                asyncMethods.addBookingLocation(BookingDetailsConvertor.toModel(bookingDetailsEntity))
                notificationService.sendNotification(
                    NotificationDetailsModel(
                        userId = bookingDetailsModel.aggregatorId,
                        title = "New Booking",
                        body = "Booking for ${bookingDetailsModel.requestedExpertise}",
                        data = hashMapOf(NotificationDataKeys.Redirect.name to NotificationKeyRedirectValue.NewBooking.name)
                    )
                )

                ResponseEntity.ok(
                    ResponseModel(
                        success = true,
                        body = BookingDetailsConvertor.toModel(bookingDetailsEntity)
                    )
                )
            } catch (e: Exception) {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseModel(
                        success = false,
                        body = null, reason = e.message ?: "Something went wrong"
                    )
                )
            }
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(newBookingValid)
        }
    }

    fun getBookingById(id: String, modelList: List<String>?): ResponseEntity<*> {
        val bookingDetailsResponse = bookingRepository.findById(id)
        return if (bookingDetailsResponse.isPresent) {
            val aggregatedData = aggregateAllDetailsInBookingDetails(bookingDetailsResponse.get(), modelList)

            ResponseEntity.ok(
                ResponseModel(
                    success = true, body = aggregatedData
                )
            )
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseModel(success = false, reason = "booking id doesn't exist", body = null))
        }
    }

    fun getAllBookingAssociatedWithProvider(
        providerToken: String,
        searchText: String?,
        bookingFilter: String?,
        modelList: List<String>?,
        page: Int,
        pageSize: Int?
    ): ResponseEntity<*> {
        if (!searchText.isNullOrEmpty()) {
            return getAllBookingsBySearchField(
                providerToken, searchText,
                modelList, bookingFilter, page, pageSize
            )
        }

        val pageable: Pageable = PageRequest.of(
            page - 1,
            pageSize ?: ApplicationConstants.PAGE_SIZE,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )
        val username = jwtTokenUtil.getUsernameFromToken(providerToken)
        val filteredStatusList = BookingDetailsConvertor.getBookingStatusList(bookingFilter)

        val totalCount =
            bookingRepository.countByAggregatorIdAndFilter(username, filteredStatusList)
        val bookingsList = bookingRepository.findAllByAggregatorIdAndFilter(username, filteredStatusList, pageable)
            .map {
                aggregateAllDetailsInBookingDetails(it, modelList)
            }
        return ResponseEntity.ok()
            .body(ResponseModel(success = true, body = mapOf("total_count" to totalCount, "bookings" to bookingsList)))
    }

    fun getAllBookingsBySearchField(
        providerToken: String,
        searchText: String, modelList: List<String>?, bookingFilter: String?, page: Int, pageSize: Int?
    ): ResponseEntity<*> {
        val bookingDetailsEntityList = mutableListOf<BookingDetailsEntity>()

        //Assuming Search filed is patient name
        val associatedPatientList = patientRepository.findByStartingWithName(searchText)
        associatedPatientList.forEach {
            bookingDetailsEntityList.addAll(bookingRepository.findAllByPatientId(it.id!!))
        }

        //Assuming SearchField is bookingId
        val bookingDetailsMatchedWithId = bookingRepository.findById(searchText)
        if (bookingDetailsMatchedWithId.isPresent) {
            bookingDetailsEntityList.add(bookingDetailsMatchedWithId.get())
        }

        val filteredStatusList = BookingDetailsConvertor.getBookingStatusList(bookingFilter)

        val username = jwtTokenUtil.getUsernameFromToken(providerToken)

        val filteredBookingEntityList = bookingDetailsEntityList.filter {
            filteredStatusList.contains(it.currentStatus) && it.aggregatorId == username
        }.sortedByDescending {
            it.createdAt
        }

        val startingIndex = (page - 1) * (pageSize ?: ApplicationConstants.PAGE_SIZE)
        val lastIndex = page * (pageSize ?: ApplicationConstants.PAGE_SIZE)

        val pageableBookingList = mutableListOf<BookingDetailsEntity>()
        if (startingIndex < filteredBookingEntityList.size) {
            pageableBookingList.addAll(
                filteredBookingEntityList.subList(
                    startingIndex,
                    min(lastIndex, filteredBookingEntityList.size)
                )
            )
        }

        val bookingsList = pageableBookingList
            .map {
                aggregateAllDetailsInBookingDetails(it, modelList)
            }

        return ResponseEntity.ok()
            .body(
                ResponseModel(
                    success = true,
                    body = mapOf("total_count" to filteredBookingEntityList.size, "bookings" to bookingsList)
                )
            )
    }

    fun fetchAllBookingsAssociatedWithUser(
        userToken: String, modelList: List<String>?,
        bookingFilter: String?,
        page: Int,
        pageSize: Int?
    ): ResponseEntity<*> {
        val pageable: Pageable = PageRequest.of(
            page - 1,
            pageSize ?: ApplicationConstants.PAGE_SIZE,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )
        val username = jwtTokenUtil.getUsernameFromToken(userToken)

        val filteredStatusList = BookingDetailsConvertor.getBookingStatusList(bookingFilter)

        val bookingsList = if (filteredStatusList.isNotEmpty()) {
            bookingRepository.findAllByUserIdAndFilter(username, filteredStatusList, pageable)
                .map {
                    aggregateAllDetailsInBookingDetails(it, modelList)
                }
        } else {
            bookingRepository.findAllByUserId(username, pageable)
                .map {
                    aggregateAllDetailsInBookingDetails(it, modelList)
                }
        }

        return ResponseEntity.ok().body(ResponseModel(success = true, body = mapOf("bookings" to bookingsList)))
    }

    fun aggregateAllDetailsInBookingDetails(
        bookingDetailsEntity: BookingDetailsEntity,
        modelList: List<String>?
    ): BookingDetailsModel {
        val bookingDetailsModel = BookingDetailsConvertor.toModel(bookingDetailsEntity)
        if (modelList?.contains("patient_id") == true && !bookingDetailsEntity.patientId.isNullOrEmpty()) {
            val patientDetails = patientRepository.findById(bookingDetailsEntity.patientId!!)
            if (patientDetails.isPresent) {
                bookingDetailsModel.patientDetails = PatientDetailsObject.toModel(patientDetails.get())
            }
        }
        if (modelList?.contains("doctor_id") == true && !bookingDetailsEntity.doctorId.isNullOrEmpty()) {
            val doctorDetails = doctorsDataRepository.findById(bookingDetailsEntity.doctorId!!)
            if (doctorDetails.isPresent) {
                bookingDetailsModel.doctorDetails = DoctorDataObject.toModel(doctorDetails.get())
            }
        }
        if (modelList?.contains("driver_id") == true && !bookingDetailsEntity.driverId.isNullOrEmpty()) {
            val driverDetails = driverDataRepository.findById(bookingDetailsEntity.driverId!!)
            if (driverDetails.isPresent) {
                bookingDetailsModel.driverDetails = DriverDataObject.toModel(driverDetails.get())
            }
        }
        if (modelList?.contains("nurse_id") == true && !bookingDetailsEntity.nurseId.isNullOrEmpty()) {
            val nurseDetails = doctorsDataRepository.findById(bookingDetailsEntity.nurseId!!)
            if (nurseDetails.isPresent) {
                bookingDetailsModel.nurseDetails = DoctorDataObject.toModel(nurseDetails.get())
            }
        }
        if (modelList?.contains("address_id") == true && !bookingDetailsEntity.addressId.isNullOrEmpty()) {
            val addressDetails = addressDetailsRepository.findById(bookingDetailsEntity.addressId!!)
            if (addressDetails.isPresent) {
                bookingDetailsModel.addressDetails = AddressDetailsConvertor.toModel(addressDetails.get())
            }
        }
        return bookingDetailsModel
    }

    fun updateBookingDetails(bookingDetailsModel: BookingDetailsModel): ResponseEntity<*> {
        val bookingDetailsEntity = bookingRepository.findById(bookingDetailsModel.id!!)
        if (!bookingDetailsModel.doctorId.isNullOrEmpty()) {
            if(bookingDetailsEntity.get().bookingType == BookingType.HomeBooking.name) {
                notificationService.sendNotification(
                    NotificationDetailsModel(
                        userId = bookingDetailsEntity.get().userId,
                        title = "Booking Confirmed",
                        body = "We have assigned your Doctor"
                    )
                )
                val latestBookingStatus = addBookingStatus(StatusOfBooking.DoctorAssigned)
                asyncMethods.updateBookingStatusInLocationEntity(latestBookingStatus, bookingDetailsModel.id!!)
                bookingDetailsEntity.get().bookingStatusList?.add(latestBookingStatus)
//                val confirmBookingValidResponse = BookingDetailsConvertor.isConfirmBookingRequestValid(bookingDetailsModel)
//                if (confirmBookingValidResponse.success == true) {
//                    notificationService.sendNotification(
//                        NotificationDetailsModel(
//                            userId = bookingDetailsEntity.get().userId,
//                            title = "Booking Confirmed",
//                            body = "We have assigned your Doctor"
//                        )
//                    )
//                    val latestBookingStatus = addBookingStatus(StatusOfBooking.DoctorAssigned)
//                    asyncMethods.updateBookingStatusInLocationEntity(latestBookingStatus, bookingDetailsModel.id!!)
//                    bookingDetailsEntity.get().bookingStatusList?.add(latestBookingStatus)
//                } else {
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(confirmBookingValidResponse)
//                }
            } else if(bookingDetailsEntity.get().bookingType == BookingType.Teleconsultation.name) {
                val latestBookingStatus = addBookingStatus(StatusOfBooking.DoctorAssigned)
                bookingDetailsEntity.get().bookingStatusList?.add(latestBookingStatus)
            }
        }

        if (!bookingDetailsModel.addressId.isNullOrEmpty()) {
            notificationService.sendNotification(
                NotificationDetailsModel(
                    userId = bookingDetailsEntity.get().userId,
                    title = "Address Updated",
                    body = "You have changed your booking location"
                )
            )
        }
//
//        if(!bookingDetailsModel.requestedExpertise.isNullOrEmpty()) {
//            //TODO: send requested expertise update notification to patient
//        }
//
//        if(bookingDetailsModel.totalAmount != null) {
//            //TODO: send billing amount notification to patient
//        }
//
        if (bookingDetailsModel.scheduledTime != null) {
            notificationService.sendNotification(
                NotificationDetailsModel(
                    userId = bookingDetailsEntity.get().userId,
                    title = "Scheduled Updated",
                    body = "Doctor will arrive on updated time"
                )
            )
        }

        if (bookingDetailsModel.currentStatus != null) {
            val status = BookingDetailsConvertor.getStatusOfBooking(bookingDetailsModel.currentStatus ?: "")
            notificationService.sendNotification(
                NotificationDetailsModel(
                    userId = bookingDetailsEntity.get().userId,
                    title = "Booking Status update",
                    body = "your booking status is ${bookingDetailsModel.currentStatus}"
                )
            )
            if (status == StatusOfBooking.Invalid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseModel(
                        success = false,
                        reason = "incorrect status", body = null
                    )
                )
            } else {
                val latestBookingStatus = addBookingStatus(status)
                asyncMethods.updateBookingStatusInLocationEntity(latestBookingStatus, bookingDetailsModel.id!!)
                bookingDetailsEntity.get().bookingStatusList?.add(latestBookingStatus)
            }
        }

        if (bookingDetailsModel.doctorNotes != null) {
            notificationService.sendNotification(
                NotificationDetailsModel(
                    userId = bookingDetailsEntity.get().userId,
                    title = "Doctor instructions",
                    body = "please check instruction added by doctor"
                )
            )
        }

        bookingRepository.save(BookingDetailsConvertor.toEntity(bookingDetailsModel, bookingDetailsEntity.get()))
        return ResponseEntity.ok(ResponseModel(success = true, body = null))
    }

    fun getBookingAmount(): ResponseEntity<*> {
        val bookingAmount = ApplicationConstants.BOOKING_AMOUNT
        return ResponseEntity.ok(ResponseModel(success = true, body = mapOf("booking_amount" to bookingAmount)))
    }

//    @CachePut(value = [ApplicationConstants.REDIS_LOCATION_CACHE_NAME], key = "#a0.id")
//    fun addBookingLocation(bookingDetailsModel: BookingDetailsModel): BookingLocationEntity {
//        val bookingLocationModel = BookingLocationModel()
//        val addressEntity = addressDetailsRepository.findById(bookingDetailsModel.addressId!!)
//        bookingLocationModel.apply {
//            bookingLocation = ApplicationConstants.FRH_AGGREGATOR_LOCATION
//            bookingId = bookingDetailsModel.id
//            patientLocation = addressEntity.get().latLong
//        }
//        return bookingLocationRepository.save(BookingLocationConvertor.toEntity(bookingLocationModel))
//    }

    @CachePut(value = [ApplicationConstants.REDIS_LOCATION_CACHE_NAME], key = "#a0.bookingId")
    fun updateBookingLocationRedis(bookingLocationModel: BookingLocationModel): BookingLocationModel? {
        return bookingLocationModel
    }

    @Cacheable(value = [ApplicationConstants.REDIS_LOCATION_CACHE_NAME], key = "#bookingId")
    fun getBookingLocationRedis(bookingId: String): BookingLocationModel? {
        val bookingLocationResponse = bookingLocationRepository.findByBookingId(bookingId)
        return if (bookingLocationResponse.isPresent) {
            BookingLocationConvertor.toModel(bookingLocationResponse.get())
        } else {
            null
        }
    }

    fun updateBookingLocation(bookingLocationModel: BookingLocationModel) {
        if (BookingLocationConvertor.isUpdateBookingModelValid(bookingLocationModel)) {
            val bookingLocationResponse = bookingLocationRepository.findByBookingId(bookingLocationModel.bookingId!!)
            if (bookingLocationResponse.isPresent) {
                val bookingLocationEntity = bookingLocationResponse.get()
                if (bookingLocationModel.lastUpdated!! > bookingLocationEntity.modifiedAt) {
                    bookingLocationEntity.bookingLocation = bookingLocationModel.bookingLocation
                    bookingLocationEntity.directionResponse = bookingLocationModel.directionResponse
                    bookingLocationRepository.save(bookingLocationEntity)
                }
            }
        }
    }

    fun getBookingLocation(bookingId: String): ResponseEntity<*> {
        val bookingLocationResponse = bookingLocationRepository.findByBookingId(bookingId)
        return if (bookingLocationResponse.isPresent) {
            ResponseEntity.ok(
                ResponseModel(
                    success = true,
                    body = BookingLocationConvertor.toModel(bookingLocationResponse.get())
                )
            )
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseModel(
                    success = false,
                    reason = "booking id doesn't exist", body = null
                )
            )
        }
    }

    private fun addBookingStatus(status: StatusOfBooking): BookingStatus {
        return BookingStatus(
            status.name, Date()
        )
    }
}