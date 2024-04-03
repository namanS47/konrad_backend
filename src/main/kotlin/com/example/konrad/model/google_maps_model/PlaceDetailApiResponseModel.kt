package com.example.konrad.model.google_maps_model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

data class PlaceDetailApiResponseModel(
    val result: Result,
    val status: String,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Result(
    val addressComponents: List<AddressComponent>,
    val geometry: Geometry,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class AddressComponent(
    val longName: String,
    val shortName: String,
    val types: List<String>,
)

data class Geometry(
    val location: Location,
)

data class Location(
    val lat: Double,
    val lng: Double,
)