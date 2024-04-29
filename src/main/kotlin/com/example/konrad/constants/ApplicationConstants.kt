package com.example.konrad.constants

import com.example.konrad.model.LatLong

object ApplicationConstants {
    const val HOME_BOOKING_AMOUNT_GENERAL_PHYSICIAN: Double = 300.0
    const val HOME_BOOKING_AMOUNT_PEDIATRICIAN: Double = 300.0

    const val TELECONSULTATION_BOOKING_AMOUNT_GENERAL_PHYSICIAN = 200.0
    const val TELECONSULTATION_BOOKING_AMOUNT_PEDIATRICIAN = 200.0

    val FRH_AGGREGATOR_LOCATION = LatLong( 25.1095703617415, 55.1840689035426)

    const val REDIS_LOCATION_CACHE_NAME = "location"
    const val PAGE_SIZE = 10
}