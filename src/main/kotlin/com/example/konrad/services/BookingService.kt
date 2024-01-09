package com.example.konrad.services

import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.constants.ApplicationConstants
import com.example.konrad.entity.BookingLocationEntity
import com.example.konrad.model.*
import com.example.konrad.repositories.BookingLocationRepository
import com.example.konrad.repositories.BookingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.Date

@Service
class BookingService(
        @Autowired private val bookingRepository: BookingRepository,
        @Autowired private val bookingLocationRepository: BookingLocationRepository,
        @Autowired private val jwtTokenUtil: JwtTokenUtil
) {
    fun addNewBooking(bookingDetailsModel: BookingDetailsModel, userToken: String): ResponseEntity<*> {
        bookingDetailsModel.userId = jwtTokenUtil.getUsernameFromToken(userToken)
        val newBookingValid = BookingDetailsConvertor.isNewBookingValid(bookingDetailsModel)
        return if (newBookingValid.success == true) {
            try {
                //TODO: Add aggregator id here and send notification to aggregator
                bookingDetailsModel.aggregatorId = "sp1username"

                bookingDetailsModel.bookingAmount = ApplicationConstants.BOOKING_AMOUNT

                //Add booking status
                bookingDetailsModel.bookingStatusList = mutableListOf(addBookingStatus(StatusOfBooking.BookingConfirmed))

                val bookingDetailsEntity = bookingRepository.save(BookingDetailsConvertor.toEntity(bookingDetailsModel, null))

                //Add frh location as initial booking location
                addBookingLocation(BookingDetailsConvertor.toModel(bookingDetailsEntity))

                ResponseEntity.ok(ResponseModel(success = true, body = null))
            } catch (e: Exception) {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseModel(success = false,
                    body = null, reason = e.message ?: "Something went wrong"))
            }
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(newBookingValid)
        }
    }

    fun getAllBookingAssociatedWithProvider(providerToken: String): ResponseEntity<*> {
        val username = jwtTokenUtil.getUsernameFromToken(providerToken)
        val bookingsList = bookingRepository.findAllByAggregatorId(username)
        return ResponseEntity.ok().body(ResponseModel(success = true, body = bookingsList.map {
            BookingDetailsConvertor.toModel(it)
        }))
    }

    fun confirmBooking(bookingDetailsModel: BookingDetailsModel): ResponseEntity<*> {
        val confirmBookingValidResponse = BookingDetailsConvertor.isConfirmBookingRequestValid(bookingDetailsModel)
        return if (confirmBookingValidResponse.success == true) {
            //TODO: send confirm booking notification to patient
            val bookingDetailsEntity = bookingRepository.findById(bookingDetailsModel.id!!)
            bookingDetailsEntity.get().bookingStatusList?.add(addBookingStatus(StatusOfBooking.DoctorAssigned))
            bookingRepository.save(BookingDetailsConvertor.toEntity(bookingDetailsModel, bookingDetailsEntity.get()))
            ResponseEntity.ok(ResponseModel(success = true, body = null))
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(confirmBookingValidResponse)
        }
    }

    fun getBookingAmount(): ResponseEntity<*> {
        val bookingAmount = ApplicationConstants.BOOKING_AMOUNT
        return ResponseEntity.ok(ResponseModel(success = true, body = mapOf("booking_amount" to bookingAmount)))
    }

    fun updateBookingStatus(bookingDetailsModel: BookingDetailsModel): ResponseEntity<*> {
        return if (bookingDetailsModel.id.isNullOrEmpty() || bookingDetailsModel.currentStatus.isNullOrEmpty()) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseModel(success = false,
                    reason = "id and status can not be empty", body = null))
        } else {
            val status = BookingDetailsConvertor.getStatusOfBooking(bookingDetailsModel.currentStatus ?: "")
            if (status == StatusOfBooking.Invalid) {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseModel(success = false,
                        reason = "incorrect status", body = null))
            } else {
                val bookingDetailsEntity = bookingRepository.findById(bookingDetailsModel.id!!)
                bookingDetailsEntity.get().bookingStatusList?.add(addBookingStatus(status))
                bookingRepository.save(BookingDetailsConvertor.toEntity(bookingDetailsModel, bookingDetailsEntity.get()))
                ResponseEntity.ok(ResponseModel(success = true, body = null))
            }
        }
    }

    @CachePut(value = [ApplicationConstants.REDIS_LOCATION_CACHE_NAME], key = "#a0.id")
    fun addBookingLocation(bookingDetailsModel: BookingDetailsModel): BookingLocationEntity {
        val bookingLocationModel = BookingLocationModel()
        bookingLocationModel.apply {
            bookingLocation = ApplicationConstants.FRH_AGGREGATOR_LOCATION
            bookingId = bookingDetailsModel.id
        }
        return bookingLocationRepository.save(BookingLocationConvertor.toEntity(bookingLocationModel))
    }

    @CachePut(value = [ApplicationConstants.REDIS_LOCATION_CACHE_NAME], key = "#a0.bookingId")
    fun updateBookingLocation(bookingLocationModel: BookingLocationModel): BookingLocationModel? {
        return bookingLocationModel
//        if(BookingLocationConvertor.isUpdateBookingModelValid(bookingLocationModel)) {
//            val bookingLocationResponse = bookingLocationRepository.findByBookingId(bookingLocationModel.bookingId!!)
//            if(bookingLocationResponse.isPresent) {
//                val bookingLocationEntity = bookingLocationResponse.get()
//                bookingLocationEntity.bookingLocation = bookingLocationModel.bookingLocation
//                return bookingLocationRepository.save(bookingLocationEntity)
//            }
//        }
//        return BookingLocationEntity()
    }

    @Cacheable(value = [ApplicationConstants.REDIS_LOCATION_CACHE_NAME], key = "#bookingId")
    fun getBookingLocationRedis(bookingId: String): BookingLocationModel? {
        val bookingLocationResponse = bookingLocationRepository.findByBookingId(bookingId)
        return if(bookingLocationResponse.isPresent) {
            BookingLocationConvertor.toModel(bookingLocationResponse.get())
        } else {
            null
        }
    }

    fun getBookingLocation(bookingId: String): ResponseEntity<*> {
        val bookingLocationResponse = bookingLocationRepository.findByBookingId(bookingId)
        return if(bookingLocationResponse.isPresent) {
            ResponseEntity.ok(ResponseModel(success = true,
                    body = BookingLocationConvertor.toModel(bookingLocationResponse.get())))
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseModel(success = false,
                    reason = "booking id doesn't exist", body= null))
        }
    }

    private fun addBookingStatus(status: StatusOfBooking): BookingStatus {
        return BookingStatus(
                status.name, Date()
        )
    }
}