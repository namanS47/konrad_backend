package com.example.konrad.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class SpAdminDataModel (
        val spAdminId: String? = null,
        val name: String? = null,
        val userName: String? = null,
        val password: String? = null,
)