package com.example.konrad.model.jwt_models

import com.example.konrad.entity.UserDetailsEntity
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDetailsModel (
        var userId: String? = null,
        var name: String? = null,
        var username: String? = null,
        var password: String? = null,
        var enabled: Boolean? = null,
        var roles: List<String>? = null,
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
        }
        return entity
    }
}