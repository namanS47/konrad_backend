package com.example.konrad.services.teleconsultation

import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.model.ResponseModel
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*


@Service
class TeleconsultationService(
    @Autowired private val jwtTokenUtil: JwtTokenUtil
) {
    @Value("\${zoom.sdk.key}")
    private lateinit var zoomSdkKey: String

    @Value("\${zoom.sdk.secret}")
    private lateinit var zoomSdkSecret: String

    fun createZoomJwtToken(token: String, sessionName: String): ResponseEntity<*> {
        val userId = jwtTokenUtil.getUsernameFromToken(token)

        val headerMap: MutableMap<String, Any> = HashMap()
        headerMap["typ"] = "JWT"
        headerMap["alg"] = "HS256"

        val claims = hashMapOf<String, Any>()
        claims["app_key"] = zoomSdkKey
        claims["version"] = 1
        claims["user_identity"] = userId
        claims["tpc"] = sessionName
        claims["role_type"] = (0)
        claims["cloud_recording_option"] = 1
        val generatedToken = Jwts.builder().setClaims(claims)
            .setIssuedAt(Date(System.currentTimeMillis() - 5*60*1000))
            .setExpiration(Date(System.currentTimeMillis() + 60 * 60 * 24 * 1 * 1000))
            .signWith(SignatureAlgorithm.HS256,Base64.getEncoder().encodeToString(zoomSdkSecret.toByteArray()))
            .setHeader(headerMap).compact()
        return ResponseEntity.ok(ResponseModel(success = true, body = generatedToken))
    }
}