package com.example.konrad.entity

import com.example.konrad.model.LatLong
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "driver_details")
class DriverDataEntity (
        @Field("user_id")
        var userId: String? = null,
        var username: String? = null,
        var name: String? = null,
        var password: String? = null,
        @Field("contact_number")
        var contactNumber: String? = null,
        @Field("country_code")
        var countryCode: String? = null,
        var location: LatLong? = null,
        @Field("profile_picture_url")
        var profilePictureUrl: String? = null,
        @Field("associated_sp_id")
        var associatedSPId: String? = null,
) : AppEntity()