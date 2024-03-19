package com.example.konrad.model

import com.example.konrad.entity.BookingLocationEntity
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.format.annotation.DateTimeFormat
import java.util.*

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class BookingLocationModel(
    var id: String? = null,
    var bookingId: String? = null,
    var bookingLocation: LatLong? = null,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    var lastUpdated: Date? = null,
    var directionResponse: DirectionResponse? = null,
    var patientLocation: LatLong? = null,
    var bookingStatus: BookingStatus? = null,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class DirectionResponse (
    @Field(name = "polyline_points_encoded")
    var polylinePointsEncoded: String? = null,

    @Field(name = "polyline_points_decoded")
    var polylinePointsDecoded: List<List<Double>>? = null,

    @Field(name = "total_distance")
    var totalDistance: String? = null,

    @Field(name = "total_duration")
    var totalDuration: String? = null,
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
            directionResponse = bookingLocationModel.directionResponse
            patientLocation = bookingLocationModel.patientLocation
            bookingStatus = bookingLocationModel.bookingStatus
        }
        return entity
    }

    fun toModel(bookingLocationEntity: BookingLocationEntity): BookingLocationModel {
        val model = BookingLocationModel()
        model.apply {
            id = bookingLocationEntity.id
            bookingId = bookingLocationEntity.bookingId
            bookingLocation = bookingLocationEntity.bookingLocation
            lastUpdated = bookingLocationEntity.modifiedAt
            directionResponse = bookingLocationEntity.directionResponse
            patientLocation = bookingLocationEntity.patientLocation
            bookingStatus = bookingLocationEntity.bookingStatus
        }
        return model
    }

    fun isUpdateBookingModelValid(bookingLocationModel: BookingLocationModel): Boolean {
        return !(bookingLocationModel.bookingLocation == null || bookingLocationModel.bookingId.isNullOrEmpty()
                || bookingLocationModel.lastUpdated == null)
    }
}