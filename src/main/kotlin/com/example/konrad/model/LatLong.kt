package com.example.konrad.model

data class LatLong (
        val latitude: Number,
        val longitude: Number
) {
    constructor() : this(0.0, 0.0)
}