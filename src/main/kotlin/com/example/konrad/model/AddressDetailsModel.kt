package com.example.konrad.model

import com.example.konrad.entity.AddressDetailsEntity
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AddressDetailsModel(
        var id: String? = null,
        var userId: String? = null,
        var addressOne: String? = null,
        var addressTwo: String? = null,
        var landmark: String? = null,
        var type: String? = null,
        var latLong: LatLong? = null,
        var description: String? = null,
)

object AddressDetailsConvertor {
    fun toEntity(addressDetailsModel: AddressDetailsModel): AddressDetailsEntity {
        val entity = AddressDetailsEntity()
        entity.apply {
            userId = addressDetailsModel.userId
            addressOne = addressDetailsModel.addressOne
            addressTwo = addressDetailsModel.addressTwo
            landmark = addressDetailsModel.landmark
            type = addressDetailsModel.type
            latLong = addressDetailsModel.latLong
            description = addressDetailsModel.description
        }
        return entity
    }

    fun toModel(addressDetailsEntity: AddressDetailsEntity): AddressDetailsModel {
        val model = AddressDetailsModel()
        model.apply {
            id = addressDetailsEntity.id
            userId = addressDetailsEntity.userId
            addressOne = addressDetailsEntity.addressOne
            addressTwo = addressDetailsEntity.addressTwo
            landmark = addressDetailsEntity.landmark
            type = addressDetailsEntity.type
            latLong = addressDetailsEntity.latLong
            description = addressDetailsEntity.description
        }
        return model
    }

    fun checkAddressValid(addressDetailsModel: AddressDetailsModel): ResponseModel<Boolean> {
        if(addressDetailsModel.userId.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "user id can not be empty")
        }
        if(addressDetailsModel.addressOne.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "address1 can not be empty")
        }
        if(addressDetailsModel.addressTwo.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "address2 can not be empty")
        }
        if(addressDetailsModel.description.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "description can not be empty")
        }
        if(addressDetailsModel.type.isNullOrEmpty()) {
            return ResponseModel(success = false, reason = "address type can not be empty")
        }
        if(addressDetailsModel.latLong == null) {
            return ResponseModel(success = false, reason = "location coordinates can not be empty")
        }

        return ResponseModel(success = true)
    }
}


