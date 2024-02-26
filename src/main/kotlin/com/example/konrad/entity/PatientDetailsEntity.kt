package com.example.konrad.entity

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "patient_details")
class PatientDetailsEntity (
        @Indexed
        @Field("user_id")
        var userId: String? = null,
        var name: String? = null,
        var age: Int? = null,
        var gender: String? = null,
        var email: String? = null,
        @Field("profile_picture_url")
        var profilePictureFileName: String? = null,
        @Field("mobile_number")
        var mobileNumber: String? = null,
        @Field("country_code")
        var countryCode: String? = null,
        @Field("relation_ship")
        var relationShip: String? = null,
        var language: String? = null,
): AppEntity()