package com.example.konrad.services.jwtService

import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.model.jwt_models.JwtResponse
import com.example.konrad.model.ResponseModel
import com.example.konrad.model.jwt_models.UserDetailsModel
import com.example.konrad.utility.StringUtils
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

    fun sendOtp(authenticationRequest: UserDetailsModel): ResponseEntity<*> {
        return try{
            if(authenticationRequest.mobileNumber.isNullOrEmpty() ||
                !StringUtils.isNumeric(authenticationRequest.mobileNumber!!) ||
                authenticationRequest.countryCode.isNullOrEmpty() ||
                !StringUtils.isNumeric(authenticationRequest.countryCode!!)) {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseModel(success = false,
                    reason = "invalid mobile number or country code", body = null))
            } else {
                //TODO: send otp to user
                ResponseEntity.ok(ResponseModel(success = true, body = null))
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseModel(success = false, reason = e.message, body = null))
        }
    }

    fun authenticationViaOtp(authenticationRequest: UserDetailsModel): ResponseEntity<*> {
        try {
            if(authenticationRequest.otp.isNullOrEmpty() ||
                !StringUtils.isNumeric(authenticationRequest.otp!!) ||
                authenticationRequest.mobileNumber.isNullOrEmpty() ||
                !StringUtils.isNumeric(authenticationRequest.mobileNumber!!) ||
                authenticationRequest.countryCode.isNullOrEmpty() ||
                !StringUtils.isNumeric(authenticationRequest.countryCode!!)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseModel(success = false,
                    reason = "invalid credentials", body = null))
            }
            //TODO: verify otp with otp service provider
            var verified = false
            if(authenticationRequest.otp == "8949") {
                verified = true
            }

            if(!verified) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseModel(success = false,
                        reason = "invalid credentials", body = null))
            }

            val userDetailsResponse = userDetailsService.getUserByMobileNumber(
                authenticationRequest.mobileNumber!!, authenticationRequest.countryCode!!)
            return if(userDetailsResponse.success == true) {
                val authTokenResponse = fetchAuthToken(userDetailsResponse.body!!.username!!)
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