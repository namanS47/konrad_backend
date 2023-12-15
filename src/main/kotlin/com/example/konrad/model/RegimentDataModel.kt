package com.example.konrad.model


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

