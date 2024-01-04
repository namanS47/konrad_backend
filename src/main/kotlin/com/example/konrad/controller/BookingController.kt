package com.example.konrad.controller

import com.example.konrad.model.BookingDetailsModel
import com.example.konrad.services.BookingService
import jakarta.annotation.security.RolesAllowed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/booking")
class BookingController(
        @Autowired private val bookingService: BookingService
) {
    @RolesAllowed("CUSTOMER")
    @PostMapping("/new")
    fun addNewBooking(@RequestHeader(name="Authorization") userToken: String,
                      @RequestBody bookingDetailsModel: BookingDetailsModel): ResponseEntity<*> {
        return bookingService.addNewBooking(bookingDetailsModel, userToken)
    }

    
}