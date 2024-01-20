package com.example.konrad.utility

import com.example.konrad.constants.ApplicationConstants
import com.example.konrad.model.BookingDetailsModel
import com.example.konrad.model.BookingLocationConvertor
import com.example.konrad.model.BookingLocationModel
import com.example.konrad.repositories.AddressDetailsRepository
import com.example.konrad.repositories.BookingLocationRepository
import com.example.konrad.services.MapsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class AsyncMethods(
    @Autowired private val addressDetailsRepository: AddressDetailsRepository,
    @Autowired private val bookingLocationRepository: BookingLocationRepository,
    @Autowired private val mapsService: MapsService
) {

    @Async
    fun addBookingLocation(bookingDetailsModel: BookingDetailsModel) {
        val bookingLocationModel = BookingLocationModel()
        val addressEntity = addressDetailsRepository.findById(bookingDetailsModel.addressId!!)

        val directionResponseFromMaps = mapsService.callDirectionApiGoogle(
            ApplicationConstants.FRH_AGGREGATOR_LOCATION,
            addressEntity.get().latLong!!
        )

        bookingLocationModel.apply {
            bookingLocation = ApplicationConstants.FRH_AGGREGATOR_LOCATION
            bookingId = bookingDetailsModel.id
            patientLocation = addressEntity.get().latLong
            directionResponse = directionResponseFromMaps
        }
        bookingLocationRepository.save(BookingLocationConvertor.toEntity(bookingLocationModel))
    }
}