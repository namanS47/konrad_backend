package com.example.konrad.model

import com.example.konrad.entity.BookingDetailsEntity
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.util.Date

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class BookingDetailsModel(
    var id: String? = null,
    var bookingIdSerialized: String? = null,
    var userId: String? = null,
    var aggregatorId: String? = null,
    var patientId: String? = null,
    var patientDetails: PatientDetailsModel? = null,
    var addressId: String? = null,
    var addressDetails: AddressDetailsModel? = null,
    var doctorId: String? = null,
    var doctorDetails: DoctorDataModel? = null,
    var driverId: String? = null,
    var driverDetails: DriverDataModel? = null,
    var nurseId: String? = null,
    var nurseDetails: DoctorDataModel? = null,
    var requestedExpertise: String? = null,
    var driverLocation: LatLong? = null,
    var bookingAmount: Double? = null,
    var totalAmount: Double? = null,
    var scheduledBooking: Boolean? = null,
    var scheduledTime: Date? = null,
    var bookingStatusList: MutableList<BookingStatus>? = null,
    var currentStatus: String? = null,
    var doctorNotes: List<String>? = null,
    var patientNotes: String? = null,
    var uploadedDocumentList: List<UploadedDocument>? = null,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class BookingStatus(
        var status: String,
        var dateTime: Date,
)

data class UploadedDocument(
        var url: String,
        var name: String,
        var type: String
)

object BookingDetailsConvertor {
    fun toEntity(bookingDetailsModel: BookingDetailsModel, bookingDetailsEntity: BookingDetailsEntity?): BookingDetailsEntity {
        val entity = bookingDetailsEntity ?: BookingDetailsEntity()
        entity.apply {
            bookingDetailsModel.userId?.let {
                userId = it
            }
            bookingDetailsModel.aggregatorId?.let {
                aggregatorId = it
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
            bookingDetailsModel.doctorNotes?.let {
                doctorNotes = it
            }
            bookingDetailsModel.patientNotes?.let {
                patientNotes = it
            }

            currentStatus = bookingStatusList?.last()?.status

            bookingDetailsModel.uploadedDocumentList?.let {
                uploadedDocumentList = it
            }
        }
        return entity
    }

    fun toModel(bookingDetailsEntity: BookingDetailsEntity): BookingDetailsModel {
        val model = BookingDetailsModel()
        model.apply {
            id = bookingDetailsEntity.id
            bookingDetailsEntity.bookingIdSerialized?.let {
                bookingIdSerialized = it
            }.run {
                bookingIdSerialized = bookingDetailsEntity.id!!.substring(bookingDetailsEntity.id!!.length - 10)
            }
            userId = bookingDetailsEntity.userId
            aggregatorId = bookingDetailsEntity.aggregatorId
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
            currentStatus = bookingDetailsEntity.currentStatus
            doctorNotes = bookingDetailsEntity.doctorNotes
            patientNotes = bookingDetailsEntity.patientNotes
            uploadedDocumentList = bookingDetailsEntity.uploadedDocumentList
        }
        return model
    }

    fun isNewBookingValid(bookingDetailsModel: BookingDetailsModel): ResponseModel<Boolean> {
        if (bookingDetailsModel.patientId.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "patientId can not be empty")
        }
        if (bookingDetailsModel.addressId.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "addressId can not be empty")
        }
//        if (bookingDetailsModel.userId.isNullOrEmpty()) {
//            return ResponseModel(success = false, reason = "userId can not be empty")
//        }
        if (!DoctorDataObject.isDoctorExpertiseValid(bookingDetailsModel.requestedExpertise)) {
            return ResponseModel(success = false, reason = "Requested expertise is invalid")
        }
        return ResponseModel(success = true)
    }

    fun isConfirmBookingRequestValid(bookingDetailsModel: BookingDetailsModel): ResponseModel<Boolean> {
        if(bookingDetailsModel.id.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "id can not be empty")
        }
        if (bookingDetailsModel.doctorId.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "doctorId can not be empty")
        }
        if (bookingDetailsModel.driverId.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "driverId can not be empty")
        }
        if (bookingDetailsModel.nurseId.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "nurseId can not be empty")
        }
        return ResponseModel(success = true)
    }

    fun getStatusOfBooking(status: String): StatusOfBooking {
        return when(status) {
            "BookingConfirmed" -> StatusOfBooking.BookingConfirmed
            "DoctorAssigned" -> StatusOfBooking.DoctorAssigned
            "DoctorOnTheWay" -> StatusOfBooking.DoctorOnTheWay
            "DoctorReached" -> StatusOfBooking.DoctorReached
            "TreatmentStarted" -> StatusOfBooking.TreatmentStarted
            "VisitCompleted" -> StatusOfBooking.VisitCompleted
            "TreatmentClosed" -> StatusOfBooking.TreatmentClosed
            "Cancelled" -> StatusOfBooking.Cancelled
            else -> {
                StatusOfBooking.Invalid
            }
        }
    }
}

enum class StatusOfBooking {
    BookingConfirmed, DoctorAssigned, DoctorOnTheWay, DoctorReached, TreatmentStarted, VisitCompleted, TreatmentClosed, Cancelled, Invalid
}

enum class BookingFilter {
    NewBooking, InProcess, Completed, Cancelled
}