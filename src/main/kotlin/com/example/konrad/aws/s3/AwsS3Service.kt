package com.example.konrad.aws.s3

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.example.konrad.config.AmazonConfig
import com.example.konrad.entity.FileUploadEntity
import com.example.konrad.model.FileUploadModel
import com.example.konrad.model.FileUploadModelConvertor
import com.example.konrad.model.ResponseModel
import com.example.konrad.repositories.FileDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.time.Instant
import java.util.*


@Service
class AwsS3Service(
    @Autowired val awsConfig: AmazonConfig,
    @Autowired val fileDetailsRepository: FileDetailsRepository
) {
    @Value("\${s3-private_bucket}")
    private lateinit var bucketName: String

    fun saveFile(
        file: MultipartFile,
        userId: String?,
        patientId: String?,
        bookingId: String?,
        title: String?,
        fileType: String?
    ): ResponseEntity<*> {
        if (fileType.isNullOrEmpty() || title.isNullOrEmpty() || !FileUploadModelConvertor.isFileTypeValid(fileType)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseModel(success = false, reason = "title or fileType is invalid or empty", body = null)
            )
        }

        val uploadFileResponse = uploadFileToPrivateBucket(file)
        return if (uploadFileResponse.success == true) {
            val fileUploadModel = FileUploadModel()
            fileUploadModel.userId = userId
            fileUploadModel.patientId = patientId
            fileUploadModel.bookingId = bookingId
            fileUploadModel.fileType = fileType
            fileUploadModel.fileName = uploadFileResponse.body
            fileUploadModel.title = title

            val savedFileDetails = fileDetailsRepository.save(FileUploadModelConvertor.toEntity(fileUploadModel))
            val savedFileDetailsModel = FileUploadModelConvertor.toModel(savedFileDetails)
            savedFileDetailsModel.fileUrl = generatePreSignedUrl(fileUploadModel.fileName)
            ResponseEntity.ok(ResponseModel(success = true, body = savedFileDetailsModel))
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uploadFileResponse)
        }
    }

    fun getFileDetails(
        userId: String?,
        patientId: String?,
        bookingId: String?,
        title: String?,
        fileType: String?
    ): ResponseEntity<*> {
        var fileDetailsList = listOf<FileUploadEntity>()
        if(!userId.isNullOrEmpty()) {
            fileDetailsList = if(!fileType.isNullOrEmpty()) {
                fileDetailsRepository.findAllByUserIdAndFileType(userId, fileType)
            } else {
                fileDetailsRepository.findAllByUserId(userId)
            }
        }
        if(!patientId.isNullOrEmpty()) {
            fileDetailsList = if(!fileType.isNullOrEmpty()) {
                fileDetailsRepository.findAllByPatientIdAndFileType(patientId, fileType)
            } else {
                fileDetailsRepository.findAllByPatientId(patientId)
            }
        } else if(!bookingId.isNullOrEmpty()) {
            fileDetailsList = if(!fileType.isNullOrEmpty()) {
                fileDetailsRepository.findAllByBookingIdAndFileType(bookingId, fileType)
            } else {
                fileDetailsRepository.findAllByBookingId(bookingId)
            }
        }

        val fileDetailsModelList =  fileDetailsList.map {
            val fileDetailsModel = FileUploadModelConvertor.toModel(it)
            fileDetailsModel.fileUrl = generatePreSignedUrl(it.fileName)
            fileDetailsModel
        }

        return ResponseEntity.ok(ResponseModel(success = true, body = mapOf("files" to fileDetailsModelList)))
    }

    fun uploadFileToPrivateBucket(file: MultipartFile): ResponseModel<String> {
        return try {
            val metadata = ObjectMetadata()
            metadata.contentLength = file.size
            var fileName = file.originalFilename?.replace(" ", "")
            fileName = Instant.now().epochSecond.toString() + fileName
            val request = PutObjectRequest(bucketName, fileName, file.inputStream, metadata)
            awsConfig.s3().putObject(request)
            //        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName)
            ResponseModel(success = true, body = fileName)
        } catch (e: Exception) {
            ResponseModel(success = false)
        }
    }

    fun generatePreSignedUrl(objectKey: String?): URL {
        val expiration = Date()
        var expTimeMillis: Long = expiration.time
        /**Generating preSigned url for 60 minutes duration validity*/
        expTimeMillis += (1000 * 60 * 60).toLong()
        expiration.setTime(expTimeMillis)
        val generatePreSignedUrlRequest = GeneratePresignedUrlRequest(bucketName, objectKey)
            .withMethod(HttpMethod.GET)
            .withExpiration(expiration)
        return awsConfig.s3().generatePresignedUrl(generatePreSignedUrlRequest)
    }
}