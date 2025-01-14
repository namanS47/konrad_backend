package com.example.konrad.entity

import com.example.konrad.model.LatLong
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "doctor_details")
class DoctorDataEntity(
        @Field("user_id")
        var userId: String? = null,
        var username: String? = null,
        var name: String? = null,
        var age: Int? = null,
        var gender: String? = null,
        var languages: List<String>? = null,
        @Field("contact_number")
        var contactNumber: String? = null,
        @Field("country_code")
        var countryCode: String? = null,
        var email: String? = null,
        var expertise: String? = null,
        var experience: String? = null,
        @Field("year_experience")
        var yearExperience: Double? = null,
        @Field("patient_treated")
        var patientTreated: Int? = null,
        var info: String? = null,
        var location: LatLong? = null,
        @Field("profile_picture_url")
        var profilePictureUrl: String? = null,
        var active: Boolean? = null,
        @Field("associated_sp_id")
        var associatedSPId: String? = null,
        var type: String? = null,
) : AppEntity()