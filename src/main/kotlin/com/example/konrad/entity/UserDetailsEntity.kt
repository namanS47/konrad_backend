package com.example.konrad.entity

import com.example.konrad.entity.AppEntity
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "user_details")
class UserDetailsEntity (
        @Indexed
        @Field(name = "user_id")
        var userId: String? = null,
        var name: String? = null,
        @Indexed
        var username: String? = null,
        var password: String? = null,
        var enabled: Boolean? = null,
        var roles: List<String>? = null,
        @Field("mobile_number")
        var mobileNumber: String? = null,
        @Field("country_code")
        var countryCode: String? = null,
): AppEntity()