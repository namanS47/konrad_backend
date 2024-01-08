package com.example.konrad.model

import com.example.konrad.entity.BookingLocationEntity
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class BookingLocationModel(
        var id: String? = null,
        var bookingId: String? = null,
        var bookingLocation: LatLong? = null,
)

object BookingLocationConvertor {
    fun toEntity(bookingLocationModel: BookingLocationModel): BookingLocationEntity {
        val entity = BookingLocationEntity()
        entity.apply {
            bookingLocationModel.id?.let {
                id = it
            }
            bookingId = bookingLocationModel.bookingId
            bookingLocation = bookingLocationModel.bookingLocation
        }
        return entity
    }

    fun toModel(bookingLocationEntity: BookingLocationEntity): BookingLocationModel {
        val model = BookingLocationModel()
        model.apply {
            id = bookingLocationEntity.id
            bookingId = bookingLocationEntity.bookingId
            bookingLocation = bookingLocationEntity.bookingLocation
        }
        return model
    }

    fun isUpdateBookingModelValid(bookingLocationModel: BookingLocationModel): Boolean {
        return !(bookingLocationModel.bookingLocation == null || bookingLocationModel.bookingId.isNullOrEmpty())
    }
}


