package com.example.konrad.services

import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.model.DoctorDataModel
import com.example.konrad.model.DoctorDataObject
import com.example.konrad.model.ResponseModel
import com.example.konrad.repositories.DoctorsDataRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class DoctorService(
        @Autowired private val doctorsDataRepository: DoctorsDataRepository,
        @Autowired private val jwtTokenUtil: JwtTokenUtil
) {
    fun getDoctorDetails(doctorToken: String): ResponseEntity<*> {
        val username = jwtTokenUtil.getUsernameFromToken(doctorToken)

        val response = getDoctorDetailsByUserName(username)

        return if(response.success == true) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
    }

    fun getDoctorDetailsByUserName(username: String): ResponseModel<DoctorDataModel> {
        val response = doctorsDataRepository.findByUsername(username)
        return if(response.isPresent) {
            ResponseModel(success = true, body = DoctorDataObject.toModel(response.get()))
        } else {
            ResponseModel(success = false, reason = "username doesn't exist", body = null)
        }
    }

    fun getDoctorDetailsByUserId(userId: String): ResponseModel<DoctorDataModel> {
        val response = doctorsDataRepository.findById(userId)
        return if(response.isPresent) {
            ResponseModel(success = true, body = DoctorDataObject.toModel(response.get()))
        } else {
            ResponseModel(success = false, reason = "username doesn't exist", body = null)
        }
    }
}