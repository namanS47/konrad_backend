package com.example.konrad.model

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
        var location: LatLong? = null,
        var profilePictureUrl: String? = null,
        var associatedSPId: String? = null,
)