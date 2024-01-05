package com.example.konrad.entity

import com.example.konrad.model.BookingStatus
import com.example.konrad.model.LatLong
import com.example.konrad.model.UploadedDocument
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document(collection = "bookings")
class BookingDetailsEntity (
        @Field(name = "booking_id")
        var bookingId: String? = null,

        @Field(name = "user_id")
        var userId: String? = null,

        @Field(name = "aggregator_id")
        var aggregatorId: String? = null,

        @Field(name = "patient_id")
        var patientId: String? = null,

        @Field(name = "address_id")
        var addressId: String? = null,

        @Field(name = "doctor_id")
        var doctorId: String? = null,

        @Field(name = "driver_id")
        var driverId: String? = null,

        @Field(name = "nurse_id")
        var nurseId: String? = null,

        @Field(name = "requested_expertise")
        var requestedExpertise: String? = null,

        @Field(name = "driver_location")
        var driverLocation: LatLong? = null,

        @Field(name = "booking_amount")
        var bookingAmount: Double? = null,

        @Field(name = "total_amount")
        var totalAmount: Double? = null,

        @Field(name = "scheduled_booking")
        var scheduledBooking: Boolean? = null,

        @Field(name = "scheduled_time")
        var scheduledTime: Date? = null,

        @Field(name = "booking_status_list")
        var bookingStatusList: MutableList<BookingStatus>? = null,

        @Field(name = "current_status")
        var currentStatus: String? = null,

        @Field(name = "uploaded_document_list")
        var uploadedDocumentList: List<UploadedDocument>? = null,
): AppEntity()