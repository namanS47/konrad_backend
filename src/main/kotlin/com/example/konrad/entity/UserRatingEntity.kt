package com.example.konrad.entity

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "user_ratings")
class UserRatingEntity(
    @Field(name = "user_id")
    var userId: String? = null,

    @Field(name = "aggregator_id")
    var aggregatorId: String? = null,

    @Field(name = "booking_id")
    var bookingId: String? = null,

    @Field(name = "patient_id")
    var patientId: String? = null,

    @Field(name = "doctor_id")
    var doctorId: String? = null,

    @Field(name = "nurse_id")
    var nurseId: String? = null,

    var rating: Double? = null,

    var review: String? = null,
): AppEntity()