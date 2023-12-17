package com.example.konrad.entity

import com.example.konrad.model.LatLong
import com.example.konrad.entity.AppEntity
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "doctor_details")
class DoctorDataEntity(
        @Field("doctor_id")
        var userId: String? = null,
        var username: String? = null,
        var name: String? = null,
        var age: Int? = null,
        var gender: String? = null,
        var languages: List<String>? = null,
        @Field("contact_number")
        var contactNumber: String? = null,
        var email: String? = null,
        var expertise: String? = null,
        var experience: String? = null,
        var info: String? = null,
        var location: LatLong? = null,
        @Field("profile_picture_url")
        var profilePictureUrl: String? = null,
        var active: Boolean? = null,
        @Field("associated_sp_id")
        var associatedSPId: String? = null,
) : AppEntity()