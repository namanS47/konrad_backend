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
        return ResponseModel(success = true)
    }
}


