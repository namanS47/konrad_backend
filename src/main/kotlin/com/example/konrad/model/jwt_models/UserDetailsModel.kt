package com.example.konrad.model.jwt_models

import com.example.konrad.entity.FcmTokenDetailsEntity
import com.example.konrad.entity.UserDetailsEntity
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.util.Date

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDetailsModel (
        var userId: String? = null,
        var name: String? = null,
        var username: String? = null,
        var password: String? = null,
        var enabled: Boolean? = null,
        var roles: List<String>? = null,
        var mobileNumber: String? = null,
        var countryCode: String? = null,
        var otp: String? = null,
        var fcmToken: List<FcmTokenDetailsModel>? = null,
)

object UserDetailsConvertor {
    fun toEntity(userDetailsModel: UserDetailsModel): UserDetailsEntity {
        val entity = UserDetailsEntity()
        entity.apply {
            userId = userDetailsModel.userId
            name = userDetailsModel.name
            username = userDetailsModel.username
            password = userDetailsModel.password
            enabled = userDetailsModel.enabled
            roles = userDetailsModel.roles
            mobileNumber = userDetailsModel.mobileNumber
            countryCode = userDetailsModel.countryCode
            fcmTokens = userDetailsModel.fcmToken?.map { FcmTokenDetailsConverter.toEntity(it)}?.toMutableList()
        }
        return entity
    }
}

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FcmTokenDetailsModel (
    var fcmToken: String? = null,
    var updatedAt: Date? = null,
)

object FcmTokenDetailsConverter {
    fun toEntity(fcmTokenDetailsModel: FcmTokenDetailsModel): FcmTokenDetailsEntity {
        val entity = FcmTokenDetailsEntity()
        entity.apply {
            token = fcmTokenDetailsModel.fcmToken
        }
        return entity
    }

    fun toModel(fcmTokenDetailsEntity: FcmTokenDetailsEntity): FcmTokenDetailsModel {
        val model = FcmTokenDetailsModel()
        model.apply {
            fcmToken = fcmTokenDetailsEntity.token
            updatedAt = fcmTokenDetailsEntity.modifiedAt
        }
        return model
    }
}