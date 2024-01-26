package com.example.konrad.services

import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.model.PatientDetailsModel
import com.example.konrad.model.PatientDetailsObject
import com.example.konrad.model.ResponseModel
import com.example.konrad.repositories.BookingRepository
import com.example.konrad.repositories.PatientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService(
    @Autowired private val patientRepository: PatientRepository,
    @Autowired private val jwtTokenUtil: JwtTokenUtil,
    @Autowired private val bookingRepository: BookingRepository,
) {
    fun addPatient(patientDetailsModel: PatientDetailsModel, token: String): ResponseEntity<*> {
        if (!patientDetailsModel.id.isNullOrEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseModel(
                    success = false,
                    reason = "please remove id", body = null
                )
            )
        }

        val username = jwtTokenUtil.getUsernameFromToken(token)
        patientDetailsModel.userId = username
        val isPatientDetailsValid = PatientDetailsObject.isPatientValid(patientDetailsModel)

        if (isPatientDetailsValid.success != true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(isPatientDetailsValid)
        }

        return try {
            patientRepository.save(PatientDetailsObject.toEntity(patientDetailsModel))
            ResponseEntity.ok(ResponseModel(success = true, body = null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseModel(success = false, reason = e.message, body = null))
        }
    }

    fun getAllPatientList(userToken: String): ResponseEntity<*> {
        val username = jwtTokenUtil.getUsernameFromToken(userToken)
        val patientList = patientRepository.findAllByUserId(username)
        return ResponseEntity.ok(
            ResponseModel(
                success = true,
                body = patientList.map { PatientDetailsObject.toModel(it) })
        )
    }

    fun getPatientById(id: String): ResponseEntity<*> {
        val response = patientRepository.findById(id)
        return if (response.isPresent) {
            ResponseEntity.ok(ResponseModel(success = true, body = PatientDetailsObject.toModel(response.get())))
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseModel(success = false, body = null, reason = "No patient found with this id"))
        }
    }

    fun fetchAllBookingsAssociatedWithUser(userToken: String): ResponseEntity<*> {
        val username = jwtTokenUtil.getUsernameFromToken(userToken)
        val bookingsList = bookingRepository.findAllByUserId(username)
        return ResponseEntity.ok().body(ResponseModel(success = true, body = mapOf("bookings" to bookingsList)))
    }
}