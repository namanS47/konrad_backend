package com.example.konrad.model

import com.example.konrad.entity.ServiceProviderEntity
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ServiceProviderDataModel(
        var userId: String? = null,
        var username: String? = null,
        var name: String? = null,
        var mobileNumber: String? = null,
        var password: String? = null,
)

object ServiceProviderDataConvertor {
    fun toEntity(serviceProviderDataModel: ServiceProviderDataModel): ServiceProviderEntity {
        val entity = ServiceProviderEntity()
        entity.apply {
            userId = serviceProviderDataModel.userId
            username = serviceProviderDataModel.username
            name = serviceProviderDataModel.name
            mobileNumber = serviceProviderDataModel.mobileNumber
        }
        return entity
    }

    fun toModel(serviceProviderEntity: ServiceProviderEntity): ServiceProviderDataModel {
        val model = ServiceProviderDataModel()
        model.apply {
            userId = serviceProviderEntity.userId
            username = serviceProviderEntity.username
            name = serviceProviderEntity.name
            mobileNumber = serviceProviderEntity.mobileNumber
        }
        return model
    }
}