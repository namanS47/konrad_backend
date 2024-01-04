package com.example.konrad.services

import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.model.BookingDetailsConvertor
import com.example.konrad.model.BookingDetailsModel
import com.example.konrad.model.ResponseModel
import com.example.konrad.repositories.BookingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

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
                bookingRepository.save(BookingDetailsConvertor.toEntity(bookingDetailsModel))
                ResponseEntity.ok(ResponseModel(success = true, body = null))
            } catch (e: Exception) {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseModel(success = false, body = null))
            }
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(newBookingValid)
        }
    }
}