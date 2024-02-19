package com.example.konrad.model

import com.example.konrad.entity.PatientDetailsEntity
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PatientDetailsModel(
        var id: String? = null,
        var userId: String? = null,
        var name: String? = null,
        var age: Int? = null,
        var gender: String? = null,
        var email: String? = null,
        var profilePictureUrl: String? = null,
        var mobileNumber: String? = null,
        var countryCode: String? = null,
        var relationShip: String? = null,
        var language: String? = null,
)

object PatientDetailsObject {
    fun toEntity(patientDetailsModel: PatientDetailsModel): PatientDetailsEntity {
        val entity = PatientDetailsEntity()
        entity.apply {
            userId = patientDetailsModel.userId
            name = patientDetailsModel.name
            age = patientDetailsModel.age
            gender = patientDetailsModel.gender
            email = patientDetailsModel.email
            mobileNumber = patientDetailsModel.mobileNumber
            countryCode = patientDetailsModel.countryCode
            relationShip = patientDetailsModel.relationShip
            language = patientDetailsModel.language
        }
        return entity
    }

    fun toModel(patientDetailsEntity: PatientDetailsEntity): PatientDetailsModel {
        val model = PatientDetailsModel()
        model.apply {
            id = patientDetailsEntity.id
            userId = patientDetailsEntity.userId
            name = patientDetailsEntity.name
            age = patientDetailsEntity.age
            gender = patientDetailsEntity.gender
            language = patientDetailsEntity.language
        }
        return model
    }

    fun isPatientValid(patientDetailsModel: PatientDetailsModel): ResponseModel<Boolean> {
        if(patientDetailsModel.userId.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "userid can not be empty")
        }
        if(patientDetailsModel.name.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "name can not be empty")
        }
        if(patientDetailsModel.age == null) {
            return ResponseModel(success = false, reason = "age can not be empty")
        }
        if(patientDetailsModel.gender.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "gender can not be empty")
        }
        if(patientDetailsModel.mobileNumber.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "mobile number can not be empty")
        }
        if(!isRelationshipTypeValid(patientDetailsModel.relationShip)) {
            return ResponseModel(success = false, reason = "invalid relationship type")
        }
        return ResponseModel(success = true)
    }

    private fun isRelationshipTypeValid(relationShip: String?): Boolean {
        return relationShip == PatientRelation.Myself.name ||
                relationShip == PatientRelation.Spouse.name ||
                relationShip == PatientRelation.Friend.name ||
                relationShip == PatientRelation.Other.name
    }
}

enum class PatientRelation {
    Myself, Spouse, Friend, Other
}
