package com.example.konrad.entity

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "file_uploaded")
class FileUploadEntity(
    @Field("user_id")
    var userId: String? = null,
    @Field("patient_id")
    var patientId: String? = null,
    @Field("booking_id")
    var bookingId: String? = null,
    @Field("file_bucket_path")
    var fileBucketPath: String? = null,
    var title: String? = null,
    @Field("file_type")
    var fileType: String? = null,
): AppEntity()