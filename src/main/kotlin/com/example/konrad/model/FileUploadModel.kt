package com.example.konrad.model

import com.example.konrad.entity.FileUploadEntity
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.net.URL
import java.util.Date

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class FileUploadModel (
    var id: String? = null,
    var userId: String? = null,
    var patientId: String? = null,
    var bookingId: String? = null,
    var fileName: String? = null,
    var title: String? = null,
    var fileType: String? = null,
    var updatedAt: Date? = null,
    var fileUrl: URL? = null,
)

object FileUploadModelConvertor {
    fun toModel(fileUploadEntity: FileUploadEntity): FileUploadModel {
        val model = FileUploadModel()
        model.apply {
            id = fileUploadEntity.id
            userId = fileUploadEntity.userId
            patientId = fileUploadEntity.patientId
            bookingId = fileUploadEntity.bookingId
            fileName = fileUploadEntity.fileName
            title = fileUploadEntity.title
            fileType = fileUploadEntity.fileType
            updatedAt = fileUploadEntity.modifiedAt
        }
        return model
    }

    fun toEntity(fileUploadModel: FileUploadModel): FileUploadEntity {
        val entity = FileUploadEntity()
        entity.apply {
            userId = fileUploadModel.userId
            patientId = fileUploadModel.patientId
            bookingId = fileUploadModel.bookingId
            fileName = fileUploadModel.fileName
            title = fileUploadModel.title
            fileType = fileUploadModel.fileType
        }
        return entity
    }

    fun isFileTypeValid(fileType: String): Boolean {
        return fileType == FileType.FinalReport.name ||
                fileType == FileType.Invoice.name ||
                fileType == FileType.LabTest.name ||
                fileType == FileType.Medication.name ||
                fileType == FileType.PatientInstruction.name ||
                fileType == FileType.ValidId.name ||
                fileType == FileType.ProfilePicture.name ||
                fileType == FileType.Other.name
    }
}

enum class FileType {
    FinalReport, Invoice, LabTest, Medication, PatientInstruction, ValidId, ProfilePicture, Other
}