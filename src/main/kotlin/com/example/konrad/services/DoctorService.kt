package com.example.konrad.services

import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.constants.ApplicationConstants
import com.example.konrad.model.*
import com.example.konrad.repositories.BookingRepository
import com.example.konrad.repositories.DoctorsDataRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class DoctorService(
    @Autowired private val doctorsDataRepository: DoctorsDataRepository,
    @Autowired private val jwtTokenUtil: JwtTokenUtil,
    @Autowired private val bookingService: BookingService,
    @Autowired private val bookingRepository: BookingRepository,
) {
    fun updateDoctorDetails(doctorDataModel: DoctorDataModel): ResponseEntity<*> {
        val isUpdateDetailsValidResponse = DoctorDataObject.isUpdateDoctorDetailsValid(doctorDataModel)
        if (isUpdateDetailsValidResponse.success == false) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(isUpdateDetailsValidResponse)
        }
        val doctorDataEntityResponse = doctorsDataRepository.findByUsernameOrUserId(doctorDataModel.userId!!)
        return if (doctorDataEntityResponse.isPresent) {
            var doctorDataEntity = DoctorDataObject.updateDoctorDetails(doctorDataModel, doctorDataEntityResponse.get())
            doctorDataEntity = doctorsDataRepository.save(doctorDataEntity)
            ResponseEntity.ok(ResponseModel(success = true, body = DoctorDataObject.toModel(doctorDataEntity)))
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseModel(success = false, reason = "username or userid doesn't exist", body = null))
        }
    }

    fun getDoctorDetails(doctorToken: String): ResponseEntity<*> {
        val username = jwtTokenUtil.getUsernameFromToken(doctorToken)

        val response = getDoctorDetailsByUserNameOrUserId(username)

        return if (response.success == true) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
    }

    fun getDoctorDetailsByUserNameOrUserId(value: String): ResponseModel<DoctorDataModel> {
        val response = doctorsDataRepository.findByUsernameOrUserId(value)
        return if (response.isPresent) {
            ResponseModel(success = true, body = DoctorDataObject.toModel(response.get()))
        } else {
            ResponseModel(success = false, reason = "username doesn't exist", body = null)
        }
    }

    fun getDoctorDetailsById(id: String): ResponseModel<DoctorDataModel> {
        val response = doctorsDataRepository.findById(id)
        return if (response.isPresent) {
            ResponseModel(success = true, body = DoctorDataObject.toModel(response.get()))
        } else {
            ResponseModel(success = false, reason = "username doesn't exist", body = null)
        }
    }

    fun fetchAllBookingsAssociatedWithDoctor(
        doctorToken: String, modelList: List<String>?,
        bookingFilter: String?,
        page: Int,
        pageSize: Int?
    ): ResponseEntity<*> {
        val pageable: Pageable = PageRequest.of(
            page - 1,
            pageSize ?: ApplicationConstants.PAGE_SIZE,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )
        val username = jwtTokenUtil.getUsernameFromToken(doctorToken)
        val doctorDetails = getDoctorDetailsByUserNameOrUserId(username)

        val filteredStatusList = BookingDetailsConvertor.getBookingStatusList(bookingFilter)

        val bookingsList =
            doctorDetails.body?.id?.let {
                if(filteredStatusList.isNotEmpty()) {
                    bookingRepository.findAllByDoctorIdAndFilter(it, filteredStatusList, pageable)
                } else {
                    bookingRepository.findAllByDoctorId(it, pageable)
                }
            }
                ?.map { bookingService.aggregateAllDetailsInBookingDetails(it, modelList) }
        return ResponseEntity.ok().body(
            ResponseModel(
                success = true, body = mapOf("bookings" to bookingsList),
                reason = doctorDetails.reason
            )
        )
    }
}