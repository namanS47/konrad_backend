package com.example.konrad.services


import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.constants.ApplicationConstants
import com.example.konrad.model.*
import com.example.konrad.model.jwt_models.UserDetailsConvertor
import com.example.konrad.model.jwt_models.UserDetailsModel
import com.example.konrad.model.jwt_models.UserRoles
import com.example.konrad.repositories.DoctorsDataRepository
import com.example.konrad.repositories.DriverDataRepository
import com.example.konrad.repositories.ServiceProviderRepository
import com.example.konrad.repositories.UserDetailsRepository
import com.example.konrad.utility.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class AggregatorService(
    @Autowired private val doctorsDataRepository: DoctorsDataRepository,
    @Autowired private val driverDataRepository: DriverDataRepository,
    @Autowired private val serviceProviderRepository: ServiceProviderRepository,
    @Autowired private val userDetailsRepository: UserDetailsRepository,
    @Autowired private val passwordEncoder: PasswordEncoder,
    @Autowired private val jwtTokenUtil: JwtTokenUtil
) {

    fun addServiceProvider(serviceProviderDataModel: ServiceProviderDataModel): ResponseEntity<*> {
        if (serviceProviderDataModel.username?.isNotEmpty() != true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseModel(success = false, body = null, reason = "User Id can not be empty"))
        }

        val response = serviceProviderDataModel.username?.let { userDetailsRepository.findByUsernameOrUserId(it) }
        if (response?.isPresent == true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseModel(success = false, reason = "username already exist", body = null))
        }

        try {
            val userDetailsModel = UserDetailsModel()
            userDetailsModel.apply {
                name = serviceProviderDataModel.name
                username = serviceProviderDataModel.username
                password = passwordEncoder.encode(serviceProviderDataModel.password)
                enabled = true
                roles = listOf(UserRoles.SERVICE_PROVIDER)
            }
            userDetailsRepository.save(UserDetailsConvertor.toEntity(userDetailsModel))

            serviceProviderRepository.save(ServiceProviderDataConvertor.toEntity(serviceProviderDataModel))
            return ResponseEntity.ok().body(ResponseModel(success = true, body = null))
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseModel(success = false, reason = "Something went wrong", body = null))
        }
    }

    fun getServiceProviderDetailsByToken(token: String): ResponseEntity<*> {
        val username = jwtTokenUtil.getUsernameFromToken(token)
        val response = getServiceProviderDetailsByUsername(username)

        return if (response.success == true) {
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }
    }

    fun getServiceProviderDetailsByUsername(username: String): ResponseModel<ServiceProviderDataModel> {
        val response = serviceProviderRepository.findByUsername(username)
        return if (response.isPresent) {
            ResponseModel(success = true, body = ServiceProviderDataConvertor.toModel(response.get()))
        } else {
            ResponseModel(success = false, reason = "username doesn't exist", body = null)
        }
    }

    fun createDoctorWithCredentials(doctorDataModel: DoctorDataModel, spToken: String): ResponseEntity<*> {
        val isDoctorValidResponse = DoctorDataObject.isDoctorDetailsValidWithCredentials(doctorDataModel)

        if (isDoctorValidResponse.success != true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(isDoctorValidResponse)
        }

        val response = doctorDataModel.username?.let { userDetailsRepository.findByUsernameOrUserId(it) }
        if (response?.isPresent == true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseModel(success = false, reason = "username already exist", body = null))
        }

        val spUsername = jwtTokenUtil.getUsernameFromToken(spToken)
        doctorDataModel.associatedSPId = spUsername

        try {
            val userDetailsModel = UserDetailsModel()
            userDetailsModel.apply {
                name = doctorDataModel.name
                username = doctorDataModel.username
                password = passwordEncoder.encode(doctorDataModel.password)
                enabled = true
                roles = listOf(UserRoles.DOCTOR)
            }
            userDetailsRepository.save(UserDetailsConvertor.toEntity(userDetailsModel))

            doctorsDataRepository.save(DoctorDataObject.toEntity(doctorDataModel))
            return ResponseEntity.ok().body(ResponseModel(success = true, body = null))
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseModel(success = false, reason = "Something went wrong", body = null))
        }
    }

    fun createDoctorWithoutCredentials(doctorDataModel: DoctorDataModel, spToken: String): ResponseEntity<*> {
        val isDoctorValidResponse = DoctorDataObject.isDoctorDetailsValidWithoutCredentials(doctorDataModel)

        if (isDoctorValidResponse.success != true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(isDoctorValidResponse)
        }

        //If user tries to create doctor by sending userId
        val response = doctorDataModel.username?.let { userDetailsRepository.findByUsernameOrUserId(it) }
        if (response?.isPresent == true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseModel(success = false, reason = "username already exist", body = null))
        }

        val spUsername = jwtTokenUtil.getUsernameFromToken(spToken)
        doctorDataModel.associatedSPId = spUsername
        val randomId = StringUtils.generateUUID()
        doctorDataModel.userId = randomId

        return try {
            val userDetailsModel = UserDetailsModel()
            userDetailsModel.apply {
                name = doctorDataModel.name
                username = randomId
                enabled = true
            }
            userDetailsRepository.save(UserDetailsConvertor.toEntity(userDetailsModel))

            doctorsDataRepository.save(DoctorDataObject.toEntity(doctorDataModel))
            ResponseEntity.ok().body(ResponseModel(success = true, body = null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseModel(success = false, reason = "Something went wrong", body = null))
        }
    }

    fun getAllDoctorsAssociatedWithSP(
        spToken: String, page: Int,
        pageSize: Int?,
        type: String?
    ): ResponseEntity<*> {
        val pageable: Pageable = PageRequest.of(
            page - 1,
            pageSize ?: ApplicationConstants.PAGE_SIZE
        )
        val spUsername = jwtTokenUtil.getUsernameFromToken(spToken)
        val totalCount = doctorsDataRepository.countByAssociatedSPIdAndType(spUsername, type ?: DoctorDataObject.TYPE_DOCTOR)
        val associatedDoctorsList =
            doctorsDataRepository.findAllByAssociatedSPIdAndType(spUsername, type ?: DoctorDataObject.TYPE_DOCTOR, pageable).map {
                DoctorDataObject.toModel(it)
            }
        return ResponseEntity.ok(ResponseModel(success = true, body = mapOf("total_count" to totalCount ,"doctor_list" to associatedDoctorsList)))
    }


    fun createDriverWithCredentials(driverDataModel: DriverDataModel, spToken: String): ResponseEntity<*> {
        val isDriverValidResponse = DriverDataObject.isDriverDetailsValidWithCredentials(driverDataModel)

        if (isDriverValidResponse.success != true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(isDriverValidResponse)
        }

        val response = driverDataModel.username?.let { userDetailsRepository.findByUsernameOrUserId(it) }
        if (response?.isPresent == true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseModel(success = false, reason = "username already exist", body = null))
        }

        val spUsername = jwtTokenUtil.getUsernameFromToken(spToken)
        driverDataModel.associatedSPId = spUsername

        try {
            val userDetailsModel = UserDetailsModel()
            userDetailsModel.apply {
                name = driverDataModel.name
                username = driverDataModel.username
                password = passwordEncoder.encode(driverDataModel.password)
                enabled = true
                roles = listOf(UserRoles.DRIVER)
            }
            userDetailsRepository.save(UserDetailsConvertor.toEntity(userDetailsModel))

            driverDataRepository.save(DriverDataObject.toEntity(driverDataModel))
            return ResponseEntity.ok().body(ResponseModel(success = true, body = null))
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseModel(success = false, reason = "Something went wrong", body = null))
        }
    }

    fun getAllDriversAssociatedWithSP(spToken: String): ResponseEntity<*> {
        val spUsername = jwtTokenUtil.getUsernameFromToken(spToken)
        val associatedDriversList = driverDataRepository.findAllByAssociatedSPId(spUsername).map {
            DriverDataObject.toModel(it)
        }
        return ResponseEntity.ok(ResponseModel(success = true, body = mapOf("driver_list" to associatedDriversList)))
    }
}