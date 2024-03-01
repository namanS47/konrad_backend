package com.example.konrad.controller

import com.example.konrad.model.BookingDetailsModel
import com.example.konrad.model.BookingLocationModel
import com.example.konrad.services.BookingService
import jakarta.annotation.security.RolesAllowed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/booking")
class BookingController(
    @Autowired private val bookingService: BookingService
) {
    @PostMapping("/new")
    fun addNewBooking(
        @RequestHeader(name = "Authorization") userToken: String,
        @RequestBody bookingDetailsModel: BookingDetailsModel
    ): ResponseEntity<*> {
        return bookingService.addNewBooking(bookingDetailsModel, userToken)
    }

    @RolesAllowed("SERVICE_PROVIDER")
    @GetMapping("/aggregator")
    fun getAllAggregatorBookings(
        @RequestHeader(name = "Authorization") aggregatorToken: String,
        @RequestHeader(name = "search") searchString: String?,
        @RequestParam("filter") bookingFilter: String?,
        @RequestParam("page") page: Int = 1,
        @RequestParam("pageSize") pageSize: Int?,
        @RequestParam("modelList") modelList: List<String>?
    ): ResponseEntity<*> {
        return bookingService.getAllBookingAssociatedWithProvider(
            aggregatorToken,
            searchString,
            bookingFilter,
            modelList,
            page,
            pageSize
        )
    }

    @GetMapping("/id")
    fun getBookingById(
        @RequestHeader(name = "id") id: String,
        @RequestParam("modelList") modelList: List<String>?
    ): ResponseEntity<*> {
        return bookingService.getBookingById(id, modelList)
    }

    @PostMapping("/update")
    fun updateBooking(@RequestBody bookingDetailsModel: BookingDetailsModel): ResponseEntity<*> {
        return bookingService.updateBookingDetails(bookingDetailsModel)
    }

    @GetMapping("/amount")
    fun fetchBookingAmount(): ResponseEntity<*> {
        return bookingService.getBookingAmount()
    }

    @PostMapping("/location")
    fun updateBookingLocation(@RequestBody bookingLocationModel: BookingLocationModel) {
        bookingService.updateBookingLocation(bookingLocationModel)
    }

    @GetMapping("/location")
    fun fetchBookingLocation(@RequestHeader bookingId: String): ResponseEntity<*> {
        return bookingService.getBookingLocation(bookingId)
//        return ResponseEntity.ok(bookingService.getBookingLocationRedis(bookingId))
    }
}