package com.example.konrad.services

import com.google.gson.GsonBuilder
import com.google.maps.DistanceMatrixApi
import com.google.maps.GeoApiContext
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode
import org.springframework.stereotype.Service


@Service
class DistanceMatrixServices {
    private val context: GeoApiContext = GeoApiContext.Builder().apiKey("AIzaSyBuWEuu3K0HznvBBh4T0kS6JbEiOLrqtEU").build()
     fun getDistance() {
         val origins = arrayOf(LatLng(25.118806424734476, 55.20211003696229), LatLng(25.07286501714357, 55.143164776554144))
         val dest = arrayOf(LatLng(25.109550931862618, 55.18413327655528))
         val v = DistanceMatrixApi.newRequest(context)
         v.mode(TravelMode.DRIVING)
         v.origins(*origins)
         v.destinations(*dest)
         val trix = v.await()
         val gson = GsonBuilder().setPrettyPrinting().create()
         print(gson.toJson(trix))
     }
}