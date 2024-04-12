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
    var fileBucketPath: String? = null,
    var title: String? = null,
    var fileType: String? = null,
    var fileFormat: String? = null,
    var updatedAt: Date? = null,
    var fileUrl: URL? = null,
    var idProofType: String? = null,
)

object FileUploadModelConvertor {
    fun toModel(fileUploadEntity: FileUploadEntity): FileUploadModel {
        val model = FileUploadModel()
        model.apply {
            id = fileUploadEntity.id
            userId = fileUploadEntity.userId
            patientId = fileUploadEntity.patientId
            bookingId = fileUploadEntity.bookingId
            fileBucketPath = fileUploadEntity.fileBucketPath
            title = fileUploadEntity.title
            fileType = fileUploadEntity.fileType
            fileFormat = fileUploadEntity.fileFormat
            updatedAt = fileUploadEntity.modifiedAt
            idProofType = fileUploadEntity.idProofType
        }
        return model
    }

    fun toEntity(fileUploadModel: FileUploadModel): FileUploadEntity {
        val entity = FileUploadEntity()
        entity.apply {
            userId = fileUploadModel.userId
            patientId = fileUploadModel.patientId
            bookingId = fileUploadModel.bookingId
            fileBucketPath = fileUploadModel.fileBucketPath
            title = fileUploadModel.title
            fileType = fileUploadModel.fileType
            fileFormat = fileUploadModel.fileFormat
            idProofType = fileUploadModel.idProofType
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

    fun isFileFormatValid(fileFormat: String?): Boolean {
        return fileFormat == FileFormat.Audio.name ||
                fileFormat == FileFormat.Video.name ||
                fileFormat == FileFormat.Image.name ||
                fileFormat == FileFormat.Document.name ||
                fileFormat == FileFormat.PDF.name
    }

    fun isIdProofValid(idProof: String?): Boolean {
        return idProof == IdProofs.EmiratesIdFront.name ||
                idProof == IdProofs.EmiratesIdBack.name ||
                idProof == IdProofs.Passport.name
    }
}

enum class FileFormat {
    Audio, Video, Image, Document, PDF
}

enum class FileType {
    FinalReport, Invoice, LabTest, Medication, PatientInstruction, ValidId, ProfilePicture, Other
}

enum class IdProofs {
    Passport, EmiratesIdFront, EmiratesIdBack
}