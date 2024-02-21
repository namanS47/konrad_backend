package com.example.konrad.model

import com.example.konrad.entity.DriverDataEntity
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class DriverDataModel(
        var userId: String? = null,
        var username: String? = null,
        var name: String? = null,
        var password: String? = null,
        var contactNumber: String? = null,
        var countryCode: String? = null,
        var location: LatLong? = null,
        var profilePictureUrl: String? = null,
        var associatedSPId: String? = null,
)

object DriverDataObject {
    fun toEntity(driverDataModel: DriverDataModel): DriverDataEntity {
        val entity = DriverDataEntity()
        entity.apply {
            userId = driverDataModel.userId
            username = driverDataModel.username
            name = driverDataModel.name
            contactNumber = driverDataModel.contactNumber
            countryCode = driverDataModel.countryCode
            location = driverDataModel.location
            profilePictureUrl = driverDataModel.profilePictureUrl
            associatedSPId = driverDataModel.associatedSPId
        }
        return entity
    }
    
    fun toModel(driverDataEntity: DriverDataEntity): DriverDataModel {
        val model = DriverDataModel()
        model.apply {
            userId = driverDataEntity.userId
            username = driverDataEntity.username
            name = driverDataEntity.name
            contactNumber = driverDataEntity.contactNumber
            countryCode = driverDataEntity.countryCode
            location = driverDataEntity.location
            profilePictureUrl = driverDataEntity.profilePictureUrl
            associatedSPId = driverDataEntity.associatedSPId
        }
        return model
    }

    fun isDriverDetailsValidWithCredentials(driverDataModel: DriverDataModel): ResponseModel<Boolean> {
        if(driverDataModel.name.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "name can not be empty", body = null)
        }
        if(driverDataModel.username.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "username can not be empty", body = null)
        }
        if(driverDataModel.password.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "password can not be empty", body = null)
        }
        if(driverDataModel.contactNumber.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "contact number can not be empty", body = null)
        }
        if(driverDataModel.profilePictureUrl.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "profile can not be empty", body = null)
        }

        return ResponseModel(success = true, body = null)
    }
}