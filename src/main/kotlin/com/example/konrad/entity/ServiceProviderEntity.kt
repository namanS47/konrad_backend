package com.example.konrad.entity

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "service_provider")
class ServiceProviderEntity(
        @Field("doctor_id")
        var userId: String? = null,
        var username: String? = null,
        var name: String? = null,
        @Field("mobile_number")
        var mobileNumber: String? = null
): AppEntity()