package com.example.konrad.services

import com.example.konrad.model.DirectionApiResponseModel
import com.example.konrad.model.DirectionResponse
import com.example.konrad.model.LatLong
import com.example.konrad.model.ResponseModel
import com.example.konrad.model.google_maps_model.AutoCompleteResponseModel
import com.example.konrad.model.google_maps_model.PlaceDetailApiResponseModel
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI


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

    fun autoCompleteApi(input: String, sessionToken: String): ResponseEntity<*> {
        return try{
            val baseUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json"
            val restTemplate = RestTemplate()
            val uri: URI = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("input", input)
                .queryParam("components", "country:ae")
                .queryParam("sessiontoken", sessionToken)
                .queryParam("key", mapsKey)
                .build().toUri()
            val responseString = restTemplate.getForObject(uri, AutoCompleteResponseModel::class.java)

            ResponseEntity.ok(ResponseModel(success = true, body = responseString))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseModel(success = false, body = null, reason = "Something went wrong"))
        }
    }

    fun placeDetailsApi(placeId: String, sessionToken: String): ResponseEntity<*> {
        return try{
            val baseUrl = "https://maps.googleapis.com/maps/api/place/details/json"
            val restTemplate = RestTemplate()
            val uri: URI = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("place_id", placeId)
                .queryParam("fields", "address_component,geometry")
                .queryParam("sessiontoken", sessionToken)
                .queryParam("key", mapsKey)
                .build().toUri()
            val responseString = restTemplate.getForObject(uri, PlaceDetailApiResponseModel::class.java)

            ResponseEntity.ok(ResponseModel(success = true, body = responseString))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseModel(success = false, body = null, reason = "Something went wrong"))
        }
    }
}