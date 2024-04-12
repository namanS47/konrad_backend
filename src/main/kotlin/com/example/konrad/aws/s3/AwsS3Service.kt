package com.example.konrad.aws.s3

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.example.konrad.config.AmazonConfig
import com.example.konrad.constants.ApplicationConstants
import com.example.konrad.entity.FileUploadEntity
import com.example.konrad.model.FileType
import com.example.konrad.model.FileUploadModel
import com.example.konrad.model.FileUploadModelConvertor
import com.example.konrad.model.ResponseModel
import com.example.konrad.repositories.FileDetailsRepository
import com.example.konrad.utility.MimeTypeDetector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.time.Instant
import java.util.*


@Service
class AwsS3Service(
    @Autowired val awsConfig: AmazonConfig,
    @Autowired val fileDetailsRepository: FileDetailsRepository,
    @Autowired private  val mimeTypeDetector: MimeTypeDetector
) {
    @Value("\${s3-private_bucket}")
    private lateinit var bucketName: String

    fun saveFile(
        file: MultipartFile?,
        fileS3Path: String?,
        userId: String?,
        patientId: String?,
        bookingId: String?,
        title: String?,
        fileType: String?,
        fileFormat: String?
    ): ResponseEntity<*> {
        if (fileType.isNullOrEmpty() ||
            title.isNullOrEmpty() ||
            !FileUploadModelConvertor.isFileTypeValid(fileType) ||
            !FileUploadModelConvertor.isFileFormatValid(fileFormat)
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseModel(
                    success = false,
                    reason = "title, fileType or fileFormat is invalid or empty",
                    body = null
                )
            )
        }

        var fileS3PathMutable = fileS3Path

        if (file != null) {
            val uploadFileResponse = uploadFileToPrivateBucket(file)
            if (uploadFileResponse.success == false) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(uploadFileResponse)
            } else {
                fileS3PathMutable = uploadFileResponse.body?.fileBucketPath
            }
        } else if (fileS3Path.isNullOrEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseModel(success = false, reason = "file and bucket path both can't be empty", body = null))
        }


        val fileUploadModel = FileUploadModel()
        fileUploadModel.userId = userId
        fileUploadModel.patientId = patientId
        fileUploadModel.bookingId = bookingId
        fileUploadModel.fileType = fileType
        fileUploadModel.fileBucketPath = fileS3PathMutable
        fileUploadModel.title = title

        val savedFileDetails = fileDetailsRepository.save(FileUploadModelConvertor.toEntity(fileUploadModel))
        val savedFileDetailsModel = FileUploadModelConvertor.toModel(savedFileDetails)
        savedFileDetailsModel.fileUrl = generatePreSignedUrl(fileUploadModel.fileBucketPath)
        return ResponseEntity.ok(ResponseModel(success = true, body = savedFileDetailsModel))
    }

    fun saveBulkFile(fileUploadModelList: List<FileUploadModel>): ResponseEntity<*> {
        val fileUploadEntityList = fileUploadModelList.map {
            if (it.fileType.isNullOrEmpty() ||
                it.title.isNullOrEmpty() ||
                !FileUploadModelConvertor.isFileTypeValid(it.fileType!!) ||
                !FileUploadModelConvertor.isFileFormatValid(it.fileFormat) ||
                it.fileBucketPath.isNullOrEmpty()
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseModel(
                        success = false,
                        reason = "title, fileType or fileFormat is invalid or empty or fileBucketPath is empty",
                        body = null
                    )
                )
            }
            if(it.fileType == FileType.ValidId.name && !FileUploadModelConvertor.isIdProofValid(it.idProofType)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseModel(
                        success = false,
                        reason = "invalid idProof type",
                        body = null
                    )
                )
            }
            FileUploadModelConvertor.toEntity(it)
        }

        fileDetailsRepository.saveAll(fileUploadEntityList)
        return ResponseEntity.ok(ResponseModel(success = true, body = null))
    }

    fun getFileDetails(
        userId: String?,
        patientId: String?,
        bookingId: String?,
        title: String?,
        fileType: List<String>?,
        page: Int, pageSize: Int?
    ): ResponseEntity<*> {
        val pageable: Pageable = PageRequest.of(
            page - 1,
            pageSize ?: ApplicationConstants.PAGE_SIZE,
            Sort.by(Sort.Direction.DESC, "modifiedAt")
        )
        var fileDetailsList = listOf<FileUploadEntity>()
        if (!userId.isNullOrEmpty()) {
            fileDetailsList = if (!fileType.isNullOrEmpty()) {
                fileDetailsRepository.findAllByUserIdAndFileType(userId, fileType, pageable)
            } else {
                fileDetailsRepository.findAllByUserId(userId, pageable)
            }
        }
        if (!patientId.isNullOrEmpty()) {
            fileDetailsList = if (!fileType.isNullOrEmpty()) {
                fileDetailsRepository.findAllByPatientIdAndFileType(patientId, fileType, pageable)
            } else {
                fileDetailsRepository.findAllByPatientId(patientId, pageable)
            }
        } else if (!bookingId.isNullOrEmpty()) {
            fileDetailsList = if (!fileType.isNullOrEmpty()) {
                fileDetailsRepository.findAllByBookingIdAndFileType(bookingId, fileType, pageable)
            } else {
                fileDetailsRepository.findAllByBookingId(bookingId, pageable)
            }
        }

        val fileDetailsModelList = fileDetailsList.map {
            val fileDetailsModel = FileUploadModelConvertor.toModel(it)
            fileDetailsModel.fileUrl = generatePreSignedUrl(it.fileBucketPath)
            fileDetailsModel
        }

        return ResponseEntity.ok(ResponseModel(success = true, body = mapOf("files" to fileDetailsModelList)))
    }

    fun uploadFileToPrivateBucket(file: MultipartFile): ResponseModel<FileUploadModel> {
        return try {
            val metadata = ObjectMetadata()
            metadata.contentLength = file.size
            val originalFileName = file.originalFilename?.replace(" ", "")
            val fileName = Instant.now().epochSecond.toString() + originalFileName
            val mimeType = mimeTypeDetector.findMimeType(fileName.substringAfterLast("."))
            metadata.contentType = mimeType ?: file.contentType
            val request = PutObjectRequest(bucketName, fileName, file.inputStream, metadata)
            awsConfig.s3().putObject(request)
            //        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName)
            val fileAccessToken = generatePreSignedUrl(fileName)
            ResponseModel(
                success = true,
                body = FileUploadModel(fileBucketPath = fileName, fileUrl = fileAccessToken, title = originalFileName?.substringBeforeLast("."))
            )
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