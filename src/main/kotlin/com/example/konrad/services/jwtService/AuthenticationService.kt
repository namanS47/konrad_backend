package com.example.konrad.services.jwtService

import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.model.jwt_models.JwtResponse
import com.example.konrad.model.ResponseModel
import com.example.konrad.model.jwt_models.UserDetailsModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
        @Autowired private val authManager: AuthenticationManager,
        @Autowired private val jwtTokenUtil: JwtTokenUtil,
        @Autowired private val userDetailsService: JwtUserDetailsService
) {
    fun authentication(authenticationRequest: UserDetailsModel): ResponseEntity<*> {
        return try {
            authManager.authenticate(
                    UsernamePasswordAuthenticationToken(
                        authenticationRequest.username,
                        authenticationRequest.password
                    )
            )
            val authTokenResponse = fetchAuthToken(authenticationRequest.username!!)
            if(authTokenResponse.success == true) {
                ResponseEntity.ok(authTokenResponse)
            } else {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(authTokenResponse)
            }
        } catch (e: DisabledException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseModel(success = false, reason = "User is disabled", body = null))
        } catch (e: BadCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseModel(success = false, reason = "invalid credentials", body = null))
        }
    }

    fun authenticationViaOtp(authenticationRequest: UserDetailsModel): ResponseEntity<*> {
        try {
            //TODO: verify otp with otp service provider
            val verified = true

            if(!verified) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseModel(success = false,
                        reason = "invalid credentials", body = null))
            }

            val userDetailsResponse = userDetailsService.getUserByUserName(authenticationRequest.username!!)
            return if(userDetailsResponse.success == true) {
                val authTokenResponse = fetchAuthToken(authenticationRequest.username!!)
                ResponseEntity.ok(authTokenResponse)
            } else {
                val response = userDetailsService.addNewUserAuthenticatedViaOtp(authenticationRequest)
                if(response.success == true) {
                    val authTokenResponse = fetchAuthToken(authenticationRequest.username!!)
                    ResponseEntity.ok(authTokenResponse)
                } else {
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
                }
            }
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseModel(success = false, reason = e.message, body = null))
        }
    }

    fun fetchAuthToken(username: String): ResponseModel<JwtResponse> {
        val userDetailsResponse = userDetailsService.getUserByUserName(username)
        return if(userDetailsResponse.success == true) {
            val userDetails = userDetailsResponse.body!!
            val token = jwtTokenUtil.generateToken(userDetails, userDetails.roles?.get(0) ?: "")
            val userRole = userDetails.roles?.get(0)?.substring(5)
            ResponseModel(success = true, body = JwtResponse(token, userRole))
        } else {
            ResponseModel(success = false, reason = userDetailsResponse.reason)
        }
    }
}