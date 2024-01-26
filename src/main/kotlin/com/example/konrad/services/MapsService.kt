package com.example.konrad.services

import com.example.konrad.model.DirectionApiResponseModel
import com.example.konrad.model.DirectionResponse
import com.example.konrad.model.LatLong
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class MapsService {
    @Value(value = "\${google.maps.key}")
    private val mapsKey: String? = null

    fun callDirectionApiGoogle(origin: LatLong, destination: LatLong): DirectionResponse {
        var uri = "https://maps.googleapis.com/maps/api/directions/json?"
        uri+= "origin=${origin.latitude},${origin.longitude}"
        uri+= "&"
        uri+= "destination=${destination.latitude},${destination.longitude}"
        uri+= "&"
        uri+= "key=${mapsKey}"

        val restTemplate = RestTemplate()
        val responseString = restTemplate.getForEntity(uri, String::class.java)


        val response = Gson().fromJson(responseString.body, DirectionApiResponseModel::class.java)


        val directionResponse= DirectionResponse()
        directionResponse.apply {
            polylinePointsEncoded = response?.routes?.first()?.overviewPolyline?.points
            totalDistance = response?.routes?.first()?.legs?.first()?.distance?.text
            totalDuration = response?.routes?.first()?.legs?.first()?.duration?.text
        }
        return directionResponse
    }
}