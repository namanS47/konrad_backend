package com.example.konrad.aws.s3

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.example.konrad.config.AmazonConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.net.URL
import java.time.Instant
import java.util.*


@Service
class AwsS3Service(
        @Autowired val awsConfig: AmazonConfig
) {
    @Value("\${s3-private_bucket}")
    private lateinit var bucketName: String

    fun saveFilePrivate(file: MultipartFile) : String {
        val metadata = ObjectMetadata()
        metadata.contentLength = file.size
        var fileName = file.originalFilename?.replace(" ", "")
        fileName = Instant.now().epochSecond.toString() + fileName
        val request = PutObjectRequest(bucketName, fileName, file.inputStream, metadata)
        awsConfig.s3().putObject(request)
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName)
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