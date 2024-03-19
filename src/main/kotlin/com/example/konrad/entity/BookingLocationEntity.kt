package com.example.konrad.entity

import com.example.konrad.model.BookingStatus
import com.example.konrad.model.DirectionResponse
import com.example.konrad.model.LatLong
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "booking_location")
class BookingLocationEntity(
        @Field(name = "booking_id")
        var bookingId: String? = null,

        @Field(name = "booking_location")
        var bookingLocation: LatLong? = null,

        @Field(name = "patient_location")
        var patientLocation: LatLong? = null,

        @Field(name = "direction_response")
        var directionResponse: DirectionResponse? = null,

        @Field(name = "booking_status")
        var bookingStatus: BookingStatus? = null,
): AppEntity()