package com.example.konrad.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
class RegimentDataModel(
        val id: String? = null,
        val regimentId: String? = null,
        val doctorId: String? = null,
        val nurseId: String? = null,
        val driverId: String? = null,
        val patientId: String? = null,
        val regimentLocation: LatLong? = null,
        val isActive: Boolean? = null,
        val isDeleted: Boolean? = null,
        val status: String? = null,
)

