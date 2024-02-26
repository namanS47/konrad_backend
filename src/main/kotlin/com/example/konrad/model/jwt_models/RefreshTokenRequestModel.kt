package com.example.konrad.model.jwt_models

data class RefreshTokenRequestModel(
    val token: String,
    val userId: String,
)
