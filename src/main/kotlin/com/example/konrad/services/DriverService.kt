package com.example.konrad.services

import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.model.*
import com.example.konrad.repositories.BookingRepository
import com.example.konrad.repositories.DriverDataRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class DriverService(
    @Autowired private val driverDataRepository: DriverDataRepository,
    @Autowired private val jwtTokenUtil: JwtTokenUtil,
    @Autowired private val bookingRepository: BookingRepository,
) {
    fun getDriverDetails(driverToken: String): ResponseEntity<*> {
        val username = jwtTokenUtil.getUsernameFromToken(driverToken)

        val response = getDriverDetailsByUserNameOrUserId(username)

        return if (response.success == true) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
    }

    fun getDriverDetailsByUserNameOrUserId(value: String): ResponseModel<DriverDataModel> {
        val response = driverDataRepository.findByUsernameOrUserId(value)
        return if (response.isPresent) {
            ResponseModel(success = true, body = DriverDataObject.toModel(response.get()))
        } else {
            ResponseModel(success = false, reason = "username doesn't exist", body = null)
        }
    }

    fun getDriverDetailsById(id: String): ResponseModel<DriverDataModel> {
        val response = driverDataRepository.findById(id)
        return if (response.isPresent) {
            ResponseModel(success = true, body = DriverDataObject.toModel(response.get()))
        } else {
            ResponseModel(success = false, reason = "username doesn't exist", body = null)
        }
    }

    fun fetchAllBookingsAssociatedWithDriver(driverToken: String): ResponseEntity<*> {
        val username = jwtTokenUtil.getUsernameFromToken(driverToken)
        val driverDetails = getDriverDetailsByUserNameOrUserId(username)
        val bookingsList = driverDetails.body?.userId?.let { bookingRepository.findAllByDriverId(it) }
            ?.map { BookingDetailsConvertor.toModel(it) }
        return ResponseEntity.ok().body(ResponseModel(success = true, body = mapOf("bookings" to bookingsList),
            reason = driverDetails.reason))
    }
}