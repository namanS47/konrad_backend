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
            sendAuthToken(authenticationRequest.username!!)
        } catch (e: DisabledException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseModel(success = false, reason = "User is disabled", body = null))
        } catch (e: BadCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseModel(success = false, reason = "invalid credentials", body = null))
        }
    }

    fun authenticationViaOtp(authenticationRequest: UserDetailsModel): ResponseEntity<*> {
        return try {
            //TODO: verify otp with otp service provider
            val verified = true

            if(!verified) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseModel(success = false,
                        reason = "invalid credentials", body = null))
            }

            sendAuthToken(authenticationRequest.username!!)
        } catch(e: UsernameNotFoundException) {

            val response = userDetailsService.addNewUserAuthenticatedViaOtp(authenticationRequest)
            if(response.success != true) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
            }
            sendAuthToken(authenticationRequest.username!!)

        } catch (e: DisabledException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseModel(success = false, reason = "User is disabled", body = null))
        } catch (e: BadCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseModel(success = false, reason = "invalid credentials", body = null))
        }
    }

    fun sendAuthToken(username: String): ResponseEntity<*> {
        val userDetails = userDetailsService.getUserByUserName(username)
        val token = jwtTokenUtil.generateToken(userDetails, userDetails.roles?.get(0) ?: "")
        val userRole = userDetails.roles?.get(0)?.substring(5)
        return ResponseEntity.ok(ResponseModel(success = true, body = JwtResponse(token, userRole)))
    }
}