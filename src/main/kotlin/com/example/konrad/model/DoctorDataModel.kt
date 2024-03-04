package com.example.konrad.model

import com.example.konrad.entity.DoctorDataEntity
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class DoctorDataModel(
    var id: String? = null,
    var userId: String? = null,
    var username: String? = null,
    var name: String? = null,
    var password: String? = null,
    var age: Int? = null,
    var gender: String? = null,
    var languages: List<String>? = null,
    var contactNumber: String? = null,
    var countryCode: String? = null,
    var email: String? = null,
    var expertise: String? = null,
    var experience: String? = null,
    var info: String? = null,
    var location: LatLong? = null,
    var profilePictureUrl: String? = null,
    var active: Boolean? = null,
    var associatedSPId: String? = null,
    var type: String? = null,
)

object DoctorDataObject {
    const val TYPE_DOCTOR = "TYPE_DOCTOR"
    const val TYPE_NURSE = "TYPE_NURSE"
    fun toEntity(doctorDataModel: DoctorDataModel): DoctorDataEntity {
        val entity = DoctorDataEntity()
        entity.apply {
            userId = doctorDataModel.userId
            username = doctorDataModel.username
            name = doctorDataModel.name
            age = doctorDataModel.age
            gender = doctorDataModel.gender
            languages = doctorDataModel.languages
            contactNumber = doctorDataModel.contactNumber
            countryCode = doctorDataModel.countryCode
            email = doctorDataModel.email
            expertise = doctorDataModel.expertise
            experience = doctorDataModel.experience
            info = doctorDataModel.info
            location = doctorDataModel.location
            profilePictureUrl = doctorDataModel.profilePictureUrl
            active = doctorDataModel.active
            associatedSPId = doctorDataModel.associatedSPId
            type = doctorDataModel.type
        }
        return entity
    }

    fun toModel(doctorDataEntity: DoctorDataEntity): DoctorDataModel {
        val model = DoctorDataModel()
        model.apply {
            id = doctorDataEntity.id
            userId = doctorDataEntity.userId
            username = doctorDataEntity.username
            name = doctorDataEntity.name
            age = doctorDataEntity.age
            gender = doctorDataEntity.gender
            languages = doctorDataEntity.languages
            contactNumber = doctorDataEntity.contactNumber
            countryCode = doctorDataEntity.countryCode
            email = doctorDataEntity.email
            expertise = doctorDataEntity.expertise
            experience = doctorDataEntity.experience
            info = doctorDataEntity.info
            location = doctorDataEntity.location
            profilePictureUrl = doctorDataEntity.profilePictureUrl
            active = doctorDataEntity.active
            associatedSPId = doctorDataEntity.associatedSPId
            type = doctorDataEntity.type
        }
        return model
    }

    fun updateDoctorDetails(doctorDataModel: DoctorDataModel, doctorDataEntity: DoctorDataEntity): DoctorDataEntity {
        doctorDataEntity.apply {
            doctorDataModel.name?.let {
                name = it
            }
            doctorDataModel.age?.let {
                age = it
            }
            doctorDataModel.gender?.let {
                gender = it
            }
            doctorDataModel.languages?.let {
                languages = it
            }
            doctorDataModel.contactNumber?.let {
                contactNumber = it
            }
            doctorDataModel.countryCode?.let {
                countryCode = it
            }
            doctorDataModel.email?.let {
                email = it
            }
            doctorDataModel.expertise?.let {
                expertise = it
            }
            doctorDataModel.experience?.let {
                experience = it
            }
            doctorDataModel.info?.let {
                info = it
            }
            doctorDataModel.active?.let {
                active = it
            }
            doctorDataModel.type?.let {
                type = it
            }
        }
        return doctorDataEntity
    }

    fun isDoctorDetailsValidWithCredentials(doctorDataModel: DoctorDataModel): ResponseModel<Boolean> {
        if (doctorDataModel.name.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "name can not be empty", body = null)
        }
        if (doctorDataModel.username.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "username can not be empty", body = null)
        }
        if (doctorDataModel.password.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "password can not be empty", body = null)
        }
        if (doctorDataModel.contactNumber.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "contact number can not be empty", body = null)
        }
        if (doctorDataModel.age == null) {
            return ResponseModel(success = false, reason = "age can not be empty", body = null)
        }
        if (doctorDataModel.type != TYPE_DOCTOR && doctorDataModel.type != TYPE_NURSE) {
            return ResponseModel(success = false, reason = "incorrect type")
        }

        return ResponseModel(success = true, body = null)
    }

    fun isDoctorDetailsValidWithoutCredentials(doctorDataModel: DoctorDataModel): ResponseModel<Boolean> {
        if (!doctorDataModel.userId.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "userid is auto created")
        }
        if (doctorDataModel.name.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "name can not be empty")
        }
        if (doctorDataModel.contactNumber.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "contact number can not be empty")
        }
        if (doctorDataModel.age == null) {
            return ResponseModel(success = false, reason = "age can not be empty")
        }
        if (doctorDataModel.type != TYPE_DOCTOR && doctorDataModel.type != TYPE_NURSE) {
            return ResponseModel(success = false, reason = "incorrect type! either $TYPE_DOCTOR or $TYPE_NURSE")
        }
        if (doctorDataModel.type == TYPE_DOCTOR && !isDoctorExpertiseValid(doctorDataModel.expertise)) {
            return ResponseModel(success = false, reason = "incorrect expertise")
        }

        return ResponseModel(success = true)
    }

    fun isUpdateDoctorDetailsValid(doctorDataModel: DoctorDataModel) :ResponseModel<Boolean> {
        if(doctorDataModel.username.isNullOrEmpty() && doctorDataModel.userId.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "invalid username or userid", body = null)
        }
        if (doctorDataModel.type == TYPE_DOCTOR && !isDoctorExpertiseValid(doctorDataModel.expertise)) {
            return ResponseModel(success = false, reason = "incorrect expertise")
        }
        if (doctorDataModel.type != TYPE_DOCTOR && doctorDataModel.type != TYPE_NURSE) {
            return ResponseModel(success = false, reason = "incorrect type")
        }
        return ResponseModel(success = true)
    }

    fun isDoctorExpertiseValid(expertise: String?): Boolean {
        return expertise == DoctorExpertise.GeneralPhysician.name ||
                expertise == DoctorExpertise.Pediatrician.name ||
                expertise == DoctorExpertise.PhysioTherapist.name
    }
}

enum class DoctorExpertise {
    Pediatrician, GeneralPhysician, PhysioTherapist
}