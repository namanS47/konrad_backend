package com.example.konrad.model

import com.example.konrad.entity.BookingDetailsEntity
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.util.Date

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class BookingDetailsModel(
        var id: String? = null,
        var bookingId: String? = null,
        var userId: String? = null,
        var patientId: String? = null,
        var addressId: String? = null,
        var doctorId: String? = null,
        var driverId: String? = null,
        var nurseId: String? = null,
        var requestedExpertise: String? = null,
        var driverLocation: LatLong? = null,
        var bookingAmount: Double? = null,
        var totalAmount: Double? = null,
        var scheduledBooking: Boolean? = null,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "IST")
        var scheduledTime: Date? = null,
        var bookingStatusList: List<BookingStatus>? = null,
        var uploadedDocumentList: List<UploadedDocument>? = null,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class BookingStatus(
        var status: String,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "IST")
        var dateTime: Date,
)

data class UploadedDocument(
        var url: String,
        var name: String,
        var type: String
)

object BookingDetailsConvertor {
    fun toEntity(bookingDetailsModel: BookingDetailsModel): BookingDetailsEntity {
        val entity = BookingDetailsEntity()
        entity.apply {
            bookingDetailsModel.bookingId?.let {
                bookingId = it
            }
            bookingDetailsModel.userId?.let {
                userId = it
            }
            bookingDetailsModel.patientId?.let {
                patientId = it
            }
            bookingDetailsModel.addressId?.let {
                addressId = it
            }
            bookingDetailsModel.doctorId?.let {
                doctorId = it
            }
            bookingDetailsModel.driverId?.let {
                driverId = it
            }
            bookingDetailsModel.nurseId?.let {
                nurseId = it
            }
            bookingDetailsModel.requestedExpertise?.let {
                requestedExpertise = it
            }
            bookingDetailsModel.driverLocation?.let {
                driverLocation = it
            }
            bookingDetailsModel.bookingAmount?.let {
                bookingAmount = it
            }
            bookingDetailsModel.totalAmount?.let {
                totalAmount = it
            }
            bookingDetailsModel.scheduledBooking?.let {
                scheduledBooking = it
            }
            bookingDetailsModel.scheduledTime?.let {
                scheduledTime = it
            }
            bookingDetailsModel.bookingStatusList?.let {
                bookingStatusList = it
            }
            bookingDetailsModel.uploadedDocumentList?.let {
                uploadedDocumentList = it
            }
        }
        return entity
    }

    fun toModel(bookingDetailsEntity: BookingDetailsEntity): BookingDetailsModel {
        val model = BookingDetailsModel()
        model.apply {
            bookingId = bookingDetailsEntity.bookingId
            userId = bookingDetailsEntity.userId
            patientId = bookingDetailsEntity.patientId
            addressId = bookingDetailsEntity.addressId
            doctorId = bookingDetailsEntity.doctorId
            driverId = bookingDetailsEntity.driverId
            nurseId = bookingDetailsEntity.nurseId
            requestedExpertise = bookingDetailsEntity.requestedExpertise
            driverLocation = bookingDetailsEntity.driverLocation
            bookingAmount = bookingDetailsEntity.bookingAmount
            totalAmount = bookingDetailsEntity.totalAmount
            scheduledBooking = bookingDetailsEntity.scheduledBooking
            scheduledTime = bookingDetailsEntity.scheduledTime
            bookingStatusList = bookingDetailsEntity.bookingStatusList
            uploadedDocumentList = bookingDetailsEntity.uploadedDocumentList
        }
        return model
    }

    fun isNewBookingValid(bookingDetailsModel: BookingDetailsModel): ResponseModel<Boolean> {
        if(bookingDetailsModel.patientId.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "patientId can not be empty")
        }
        if(bookingDetailsModel.addressId.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "addressId can not be empty")
        }
        if(bookingDetailsModel.userId.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "userId can not be empty")
        }
        if(!DoctorDataObject.isDoctorExpertiseValid(bookingDetailsModel.requestedExpertise)) {
            return ResponseModel(success = false, reason = "Requested expertise is invalid")
        }
        return ResponseModel(success = true)
    }
}