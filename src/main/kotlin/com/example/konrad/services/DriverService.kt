package com.example.konrad.services

import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.constants.ApplicationConstants
import com.example.konrad.model.*
import com.example.konrad.repositories.BookingRepository
import com.example.konrad.repositories.DriverDataRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class DriverService(
    @Autowired private val driverDataRepository: DriverDataRepository,
    @Autowired private val jwtTokenUtil: JwtTokenUtil,
    @Autowired private val bookingRepository: BookingRepository,
    @Autowired private val bookingService: BookingService
) {

    fun updateDriverDetails(driverDataModel: DriverDataModel): ResponseEntity<*> {
        val isUpdateDetailsValidResponse = DriverDataObject.isUpdateDriverDetailsValid(driverDataModel)
        if (isUpdateDetailsValidResponse.success == false) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(isUpdateDetailsValidResponse)
        }

        val driverDataEntityResponse = driverDataRepository.findByUsernameOrUserId(driverDataModel.username!!)
        return if (driverDataEntityResponse.isPresent) {
            var driverDataEntity = DriverDataObject.updateDriverDetails(driverDataModel, driverDataEntityResponse.get())
            driverDataEntity = driverDataRepository.save(driverDataEntity)
            ResponseEntity.ok(ResponseModel(success = true, body = DriverDataObject.toModel(driverDataEntity)))
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseModel(success = false, reason = "username or userid doesn't exist", body = null))
        }
    }

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

    fun fetchAllBookingsAssociatedWithDriver(
        driverToken: String, modelList: List<String>?,
        bookingFilter: String?,
        page: Int,
        pageSize: Int?
    ): ResponseEntity<*> {
        val pageable: Pageable = PageRequest.of(
            page - 1,
            pageSize ?: ApplicationConstants.PAGE_SIZE,
            Sort.by(Sort.Direction.DESC, "createdAt")
        )
        val username = jwtTokenUtil.getUsernameFromToken(driverToken)
        val driverDetails = getDriverDetailsByUserNameOrUserId(username)

        val filteredStatusList = BookingDetailsConvertor.getBookingStatusList(bookingFilter)

        val bookingsList =
            driverDetails.body?.id?.let {
                if(filteredStatusList.isNotEmpty()) {
                    bookingRepository.findAllByDriverIdAndFilter(it, filteredStatusList, pageable)
                } else {
                    bookingRepository.findAllByDriverId(it, pageable)
                }
            }
                ?.map { bookingService.aggregateAllDetailsInBookingDetails(it, modelList) }
        return ResponseEntity.ok().body(
            ResponseModel(
                success = true, body = mapOf("bookings" to bookingsList),
                reason = driverDetails.reason
            )
        )
    }
}