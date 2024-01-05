package com.example.konrad.services

import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.constants.ApplicationConstants
import com.example.konrad.model.*
import com.example.konrad.repositories.BookingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.Date

@Service
class BookingService(
        @Autowired private val bookingRepository: BookingRepository,
        @Autowired private val jwtTokenUtil: JwtTokenUtil
) {
    fun addNewBooking(bookingDetailsModel: BookingDetailsModel, userToken: String): ResponseEntity<*> {
        bookingDetailsModel.userId = jwtTokenUtil.getUsernameFromToken(userToken)
        val newBookingValid = BookingDetailsConvertor.isNewBookingValid(bookingDetailsModel)
        return if (newBookingValid.success == true) {
            try {
                //TODO: Add aggregator id here and send notification to aggregator
                bookingDetailsModel.aggregatorId = "sp1username"

                bookingDetailsModel.bookingAmount = ApplicationConstants.bookingAmount
                bookingDetailsModel.bookingStatusList = mutableListOf(addBookingStatus(StatusOfBooking.BookingConfirmed))
                bookingRepository.save(BookingDetailsConvertor.toEntity(bookingDetailsModel, null))
                ResponseEntity.ok(ResponseModel(success = true, body = null))
            } catch (e: Exception) {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseModel(success = false, body = null))
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
        val bookingAmount = ApplicationConstants.bookingAmount
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

    private fun addBookingStatus(status: StatusOfBooking): BookingStatus {
        return BookingStatus(
                status.name, Date()
        )
    }
}