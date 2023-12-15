package com.example.konrad.model

data class ResponseModel<T> (
        var success: Boolean? = null,
        var reason: String? = "",
        var body: T? = null
)