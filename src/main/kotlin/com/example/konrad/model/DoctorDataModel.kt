package com.example.konrad.model

import com.example.konrad.entity.DoctorDataEntity
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class DoctorDataModel(
        var userId: String? = null,
        var username: String? = null,
        var name: String? = null,
        var password: String? = null,
        var age: Int? = null,
        var gender: String? = null,
        var languages: List<String>? = null,
        var contactNumber: String? = null,
        var email: String? = null,
        var expertise: String? = null,
        var experience: String? = null,
        var info: String? = null,
        var location: LatLong? = null,
        var profilePictureUrl: String? = null,
        var active: Boolean? = null,
        var associatedSPId: String? = null,
)

object DoctorDataObject {
    fun toEntity(doctorDataModel: DoctorDataModel): DoctorDataEntity {
        val entity = DoctorDataEntity()
        entity.apply {
            username = doctorDataModel.username
            name = doctorDataModel.name
            age = doctorDataModel.age
            gender = doctorDataModel.gender
            contactNumber = doctorDataModel.contactNumber
            email = doctorDataModel.email
            expertise = doctorDataModel.expertise
            experience = doctorDataModel.experience
            info = doctorDataModel.info
            location = doctorDataModel.location
            profilePictureUrl = doctorDataModel.profilePictureUrl
            active = doctorDataModel.active
            associatedSPId = doctorDataModel.associatedSPId
        }
        return entity
    }
    
    fun toModel(doctorDataEntity: DoctorDataEntity): DoctorDataModel {
        val model = DoctorDataModel()
        model.apply {
            userId = doctorDataEntity.id
            username = doctorDataEntity.username
            name = doctorDataEntity.name
            age = doctorDataEntity.age
            gender = doctorDataEntity.gender
            contactNumber = doctorDataEntity.contactNumber
            email = doctorDataEntity.email
            expertise = doctorDataEntity.expertise
            experience = doctorDataEntity.experience
            info = doctorDataEntity.info
            location = doctorDataEntity.location
            profilePictureUrl = doctorDataEntity.profilePictureUrl
            active = doctorDataEntity.active
            associatedSPId = doctorDataEntity.associatedSPId
        }
        return model
    }

    fun isDoctorDetailsValidWithCredentials(doctorDataModel: DoctorDataModel): ResponseModel<Boolean> {
        if(doctorDataModel.name.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "name can not be empty", body = null)
        }
        if(doctorDataModel.username.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "username can not be empty", body = null)
        }
        if(doctorDataModel.password.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "password can not be empty", body = null)
        }
        if(doctorDataModel.contactNumber.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "contact number can not be empty", body = null)
        }
        if(doctorDataModel.age == null) {
            return ResponseModel(success = false, reason = "age can not be empty", body = null)
        }
        if(doctorDataModel.profilePictureUrl.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "profile can not be empty", body = null)
        }

        return ResponseModel(success = true, body = null)


//        return !doctorDataModel.name.isNullOrEmpty() && !doctorDataModel.username.isNullOrEmpty() &&
//                !doctorDataModel.password.isNullOrEmpty() && !doctorDataModel.associatedSPId.isNullOrEmpty() &&
//                !doctorDataModel.contactNumber.isNullOrEmpty() && !doctorDataModel.gender.isNullOrEmpty() &&
//                !doctorDataModel.languages.isNullOrEmpty()
    }

    fun isDoctorDetailsValidWithoutCredentials(doctorDataModel: DoctorDataModel): ResponseModel<Boolean> {
        if(doctorDataModel.name.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "name can not be empty", body = null)
        }
        if(doctorDataModel.contactNumber.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "contact number can not be empty", body = null)
        }
        if(doctorDataModel.age == null) {
            return ResponseModel(success = false, reason = "age can not be empty", body = null)
        }
        if(doctorDataModel.profilePictureUrl.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "profile picture can not be empty", body = null)
        }

        return ResponseModel(success = true, body = null)
    }
}