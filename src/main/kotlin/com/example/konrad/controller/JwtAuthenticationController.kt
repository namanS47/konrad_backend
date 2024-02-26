package com.example.konrad.controller

import com.example.konrad.model.jwt_models.RefreshTokenRequestModel
import com.example.konrad.model.jwt_models.UserDetailsModel
import com.example.konrad.services.jwtService.AuthenticationService


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@CrossOrigin
class JwtAuthenticationController(
        @Autowired private val authenticationService: AuthenticationService
) {

    @RequestMapping(value = ["/authenticate"], method = [RequestMethod.POST])
    fun createAuthenticationToken(@RequestBody authenticationRequest: UserDetailsModel): ResponseEntity<*> {
        return authenticationService.authentication(authenticationRequest)
    }

    @RequestMapping(value = ["/authenticate/otp/request"], method = [RequestMethod.POST])
    fun requestOtp(@RequestBody authenticationRequest: UserDetailsModel): ResponseEntity<*> {
        return authenticationService.sendOtp(authenticationRequest)
    }

    @RequestMapping(value = ["/authenticate/otp"], method = [RequestMethod.POST])
    fun createAuthenticationTokenByOtp(@RequestBody authenticationRequest: UserDetailsModel): ResponseEntity<*> {
        return authenticationService.authenticationViaOtp(authenticationRequest)
    }

    @RequestMapping(value = ["/refreshToken"], method = [RequestMethod.POST])
    fun fetchAuthTokenFromRefreshToken(@RequestBody refreshTokenRequestModel: RefreshTokenRequestModel): ResponseEntity<*> {
        return authenticationService.fetchAuthTokenFromRefreshToken(refreshTokenRequestModel)
    }
}