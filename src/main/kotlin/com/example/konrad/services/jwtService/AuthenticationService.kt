package com.example.konrad.services.jwtService

import com.example.konrad.jwt_config.JwtTokenUtil
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
            val userDetails = userDetailsService.getUserByUserName(authenticationRequest.username!!)
            val token = jwtTokenUtil.generateToken(userDetails, userDetails.roles?.get(0) ?: "")
            ResponseEntity.ok<Any>(ResponseModel(success = true, body = JwtResponse(token)))
        } catch (e: DisabledException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseModel(success = false, reason = "User is disabled", body = null))
        } catch (e: BadCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseModel(success = false, reason = "invalid credentials", body = null))
        }
    }
}