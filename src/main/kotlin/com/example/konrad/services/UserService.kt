package com.example.konrad.services

import com.example.konrad.aws.s3.AwsS3Service
import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.model.*
import com.example.konrad.repositories.PatientRepository
import com.example.konrad.repositories.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class UserService(
    @Autowired private val patientRepository: PatientRepository,
    @Autowired private val jwtTokenUtil: JwtTokenUtil,
    @Autowired private val awsService: AwsS3Service,
    @Autowired private val userDetailsRepository: UserDetailsRepository
) {
    fun addPatient(patientDetailsModel: PatientDetailsModel, token: String, userId: String?): ResponseEntity<*> {
        if (!patientDetailsModel.id.isNullOrEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseModel(
                    success = false,
                    reason = "please remove id", body = null
                )
            )
        }

        val username = userId ?: jwtTokenUtil.getUsernameFromToken(token)
        patientDetailsModel.userId = username
        val isPatientDetailsValid = PatientDetailsObject.isPatientValid(patientDetailsModel)

        if (patientDetailsModel.relationShip == PatientRelation.Myself.name) {
            val patientList = patientRepository.findAllByUserId(username)
            patientList.forEach {
                if (it.relationShip == PatientRelation.Myself.name) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(
                            ResponseModel(
                                success = false,
                                reason = "Patient Profile with Myself already created",
                                body = null
                            )
                        )
                }
            }
        }

        if (isPatientDetailsValid.success != true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(isPatientDetailsValid)
        }

        return try {
            val patientDetailsEntity = patientRepository.save(PatientDetailsObject.toEntity(patientDetailsModel, null))
            ResponseEntity.ok(ResponseModel(success = true, body = PatientDetailsObject.toModel(patientDetailsEntity)))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseModel(success = false, reason = e.message, body = null))
        }
    }

    fun editPatient(patientDetailsModel: PatientDetailsModel, token: String): ResponseEntity<*> {
        val username = jwtTokenUtil.getUsernameFromToken(token)
        val roles = jwtTokenUtil.getRoleFromToken(token)
        if (patientDetailsModel.id.isNullOrEmpty() || (username != patientDetailsModel.userId && roles != "ROLE_SERVICE_PROVIDER")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseModel(
                    success = false,
                    reason = "invalid request", body = null
                )
            )
        }
        val patientDetailsEntityResponse = patientRepository.findById(patientDetailsModel.id!!)
        return if (patientDetailsEntityResponse.isPresent) {
            try {
                val patientDetailsEntity = patientRepository.save(
                    PatientDetailsObject.toEntity(
                        patientDetailsModel,
                        patientDetailsEntityResponse.get()
                    )
                )
                ResponseEntity.ok(
                    ResponseModel(
                        success = true,
                        body = PatientDetailsObject.toModel(patientDetailsEntity)
                    )
                )
            } catch (e: Exception) {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseModel(success = false, reason = e.message, body = null))
            }
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseModel(success = false, reason = "patient doesn't exist", body = null))
        }
    }

    fun getAllPatientList(userToken: String, userId: String?): ResponseEntity<*> {
        val username = userId ?: jwtTokenUtil.getUsernameFromToken(userToken)
        val patientList = patientRepository.findAllByUserId(username)
            .map {
                PatientDetailsObject.toModel(it)
            }
        return ResponseEntity.ok(
            ResponseModel(
                success = true,
                body = mapOf("patient_list" to patientList)
            )
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

    fun addPatientProfilePicture(file: MultipartFile, patientId: String): ResponseEntity<*> {
        val saveFileResponse = awsService.uploadFileToPrivateBucket(file)
        return if (saveFileResponse.success == true) {
            val patientResponse = patientRepository.findById(patientId)
            if (patientResponse.isPresent) {
                val patient = patientResponse.get()
                patient.profilePictureFileName = saveFileResponse.body?.fileBucketPath
                patientRepository.save(patient)
                ResponseEntity.ok(ResponseModel(success = true, body = null))
            } else {
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseModel(success = false, body = null, reason = "No patient found with this id"))
            }
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(saveFileResponse)
        }
    }

    fun fetchUserPatientProfile(userToken: String): ResponseEntity<*> {
        val username = jwtTokenUtil.getUsernameFromToken(userToken)
        val patientList = patientRepository.findAllByUserId(username)
        patientList.forEach {
            if (it.relationShip == PatientRelation.Myself.name) {
                return ResponseEntity.ok()
                    .body(ResponseModel(success = true, body = PatientDetailsObject.toModel(it)))
            }
        }
        return ResponseEntity.ok().body(ResponseModel(success = false, reason = "Profile not added yet", body = null))
    }
}