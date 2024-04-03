package com.example.konrad.model.google_maps_model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

data class AutoCompleteResponseModel (
    var predictions: List<Predictions>? = null,
    var status: String? = null,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Predictions (
    var description: String? = null,
    var placeId: String? = null,
    var types: List<String>? = null
)