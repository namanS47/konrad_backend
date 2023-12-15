package com.example.konrad.model

import com.example.konrad.entity.DoctorDataEntity

data class DoctorDataModel(
        var userId: String? = null,
        var name: String? = null,
        var age: Int? = null,
        var gender: String? = null,
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

object DoctorDataConvertor {
    fun toEntity(doctorDataModel: DoctorDataModel): DoctorDataEntity {
        val entity = DoctorDataEntity()
        entity.apply { 
            userId = doctorDataModel.userId
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
            userId = doctorDataEntity.userId
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
}