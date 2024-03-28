package com.example.konrad.services.jwtService

import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.entity.RefreshTokenEntity
import com.example.konrad.model.ResponseModel
import com.example.konrad.model.jwt_models.JwtResponse
import com.example.konrad.model.jwt_models.RefreshTokenRequestModel
import com.example.konrad.model.jwt_models.UserDetailsModel
import com.example.konrad.repositories.RefreshTokenRepository
import com.example.konrad.utility.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import java.time.Instant
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*


@Service
class AuthenticationService(
    @Autowired private val authManager: AuthenticationManager,
    @Autowired private val jwtTokenUtil: JwtTokenUtil,
    @Autowired private val userDetailsService: JwtUserDetailsService,
    @Autowired private val refreshTokenRepository: RefreshTokenRepository
) {
    var logger: Logger = LoggerFactory.getLogger(this::class.java)
    fun createRefreshToken(userName: String?): RefreshTokenEntity {
        val refreshToken: RefreshTokenEntity = RefreshTokenEntity()
        refreshToken.apply {
            username = userName
            tokenList = mutableListOf(UUID.randomUUID().toString())
            expiryDate = Instant.now().plusMillis(((24 * 60 * 60 * 300).toLong() * 1000)) //10 minutes
        }
        logger.debug("REFRESH TOKEN CREATED")

        return refreshTokenRepository.save(refreshToken)
    }

    fun authentication(authenticationRequest: UserDetailsModel): ResponseEntity<*> {
        return try {
            authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    authenticationRequest.username,
                    authenticationRequest.password
                )
            )
            val authTokenResponse = fetchAuthToken(authenticationRequest.username!!, null)
            if (authTokenResponse.success == true) {
                logger.debug("USER AUTHENTICATED")
                ResponseEntity.ok(authTokenResponse)
            } else {
                logger.error("AUTHENTICATION ERROR")
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(authTokenResponse)
            }
        } catch (e: DisabledException) {
            val responseModel = ResponseModel(success = false, reason = "User is disabled", body = null)
            logger.debug("AUTHENTICATION ERROR")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(responseModel)
        } catch (e: BadCredentialsException) {
            val responseModel = ResponseModel(success = false, reason = "invalid credentials", body = null)
            logger.debug("AUTHENTICATION ERROR")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(responseModel)
        }
    }

    fun sendOtp(authenticationRequest: UserDetailsModel): ResponseEntity<*> {
        return try {
            if (authenticationRequest.mobileNumber.isNullOrEmpty() ||
                !StringUtils.isNumeric(authenticationRequest.mobileNumber!!) ||
                authenticationRequest.countryCode.isNullOrEmpty() ||
                !StringUtils.isNumeric(authenticationRequest.countryCode!!)
            ) {
                logger.debug("OTP ERROR: invalid mobile number or country code")
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseModel(
                        success = false,
                        reason = "invalid mobile number or country code", body = null
                    )
                )
            } else {
                //TODO: send otp to user
                logger.debug("OTP SEND: {}", authenticationRequest.toString())
                ResponseEntity.ok(ResponseModel(success = true, body = null))
            }
        } catch (e: Exception) {
            logger.error("OTP Error: internal server error {}", authenticationRequest.toString())
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseModel(success = false, reason = e.message, body = null))
        }
    }

    fun authenticationViaOtp(authenticationRequest: UserDetailsModel): ResponseEntity<*> {
        try {
            if (authenticationRequest.otp.isNullOrEmpty() ||
                !StringUtils.isNumeric(authenticationRequest.otp!!) ||
                authenticationRequest.mobileNumber.isNullOrEmpty() ||
                !StringUtils.isNumeric(authenticationRequest.mobileNumber!!) ||
                authenticationRequest.countryCode.isNullOrEmpty() ||
                !StringUtils.isNumeric(authenticationRequest.countryCode!!)
            ) {
                logger.debug("OTP ERROR: invalid credentials")
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseModel(
                        success = false,
                        reason = "invalid credentials", body = null
                    )
                )
            }
            //TODO: verify otp with otp service provider
            var verified = false
            if (authenticationRequest.otp == "8949") {
                verified = true
            }

            if (!verified) {
                logger.debug("OTP ERROR: invalid credentials")
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseModel(
                        success = false,
                        reason = "invalid credentials", body = null
                    )
                )
            }

            val userDetailsResponse = userDetailsService.getUserByMobileNumber(
                authenticationRequest.mobileNumber!!, authenticationRequest.countryCode!!
            )
            return if (userDetailsResponse.success == true) {
                val authTokenResponse = fetchAuthToken(userDetailsResponse.body!!.userId!!, null)
                ResponseEntity.ok(authTokenResponse)
            } else {
                val response = userDetailsService.addNewUserAuthenticatedViaOtp(authenticationRequest)
                if (response.success == true) {
                    val authTokenResponse = fetchAuthToken(response.body!!.userId!!, null)
                    ResponseEntity.ok(authTokenResponse)
                } else {
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
                }
            }
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseModel(success = false, reason = e.message, body = null))
        }
    }

    fun fetchAuthTokenFromRefreshToken(refreshTokenRequestModel: RefreshTokenRequestModel): ResponseEntity<*> {
        val refreshTokenEntity = refreshTokenRepository.findByUsernameAndTokenListContaining(
            refreshTokenRequestModel.userId,
            refreshTokenRequestModel.token
        )
        if (refreshTokenEntity.isPresent) {
            /**Check for token expiration && reusing token vulnerability**/
            return if (refreshTokenEntity.get().expiryDate!! < Instant.now() ||
                refreshTokenEntity.get().tokenList?.last() != refreshTokenRequestModel.token
            ) {
                refreshTokenRepository.delete(refreshTokenEntity.get())
                ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseModel(success = false, reason = "token expired", body = null))
            } else {
                val updatedRefreshToken = refreshTokenEntity.get()
                updatedRefreshToken.tokenList?.add(UUID.randomUUID().toString())
                refreshTokenRepository.save(updatedRefreshToken)
                val authTokenResponse = fetchAuthToken(refreshTokenEntity.get().username!!, updatedRefreshToken)
                ResponseEntity.ok(authTokenResponse)
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseModel(success = false, reason = "Invalid Token", body = null))
        }
    }

    fun logout(refreshTokenRequestModel: RefreshTokenRequestModel): ResponseEntity<*> {
        val refreshTokenEntity = refreshTokenRepository.findByUsernameAndTokenListContaining(
            refreshTokenRequestModel.userId,
            refreshTokenRequestModel.token
        )
        return if (refreshTokenEntity.isPresent) {
            refreshTokenRepository.delete(refreshTokenEntity.get())
            ResponseEntity.ok().body(ResponseModel(success = true, body = null))
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseModel(success = false, reason = "Invalid Token", body = null))
        }
    }

    fun fetchAuthToken(id: String, refreshTokenEntity: RefreshTokenEntity?): ResponseModel<JwtResponse> {
        val userDetailsResponse = userDetailsService.getUserByUsernameOrUserId(id)
        val refreshToken = refreshTokenEntity ?: createRefreshToken(id)
        return if (userDetailsResponse.success == true) {
            val userDetails = userDetailsResponse.body!!
            val token = jwtTokenUtil.generateToken(id, userDetails.roles?.get(0) ?: "")
            val userRole = userDetails.roles?.get(0)?.substring(5)
            ResponseModel(success = true, body = JwtResponse(token, refreshToken.tokenList?.last(), userRole, id))
        } else {
            ResponseModel(success = false, reason = userDetailsResponse.reason)
        }
    }
}