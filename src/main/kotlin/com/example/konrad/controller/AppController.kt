package com.example.konrad.controller

import com.example.konrad.model.AddressDetailsModel
import com.example.konrad.model.PatientDetailsModel
import com.example.konrad.model.ServiceProviderDataModel
import com.example.konrad.model.jwt_models.UserDetailsModel
import com.example.konrad.services.*
import com.example.konrad.services.jwtService.JwtUserDetailsService
import jakarta.annotation.security.RolesAllowed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class AppController(
        @Autowired private val distanceMatrixServices: DistanceMatrixServices,
        @Autowired private val jwtUserDetailsService: JwtUserDetailsService,
        @Autowired private val aggregatorService: AggregatorService,
        @Autowired private val doctorService: DoctorService,
        @Autowired private val driverService: DriverService,
        @Autowired private val addressService: AddressService,
        @Autowired private val userService: UserService
) {
//    @GetMapping("/")
//    fun runDistanceMatrix() {
//        distanceMatrixServices.getDistance()
//    }

    @GetMapping("/")
    fun getHello(): String = "Hello Naman"

    @RolesAllowed("ADMIN")
    @PostMapping("/addUser")
    fun addAdmin(@RequestBody adminDataModel: UserDetailsModel): ResponseEntity<*> {
        return jwtUserDetailsService.addUser(adminDataModel)
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/admin/aggregator")
    fun addServiceProvider(@RequestBody serviceProviderDataModel: ServiceProviderDataModel): ResponseEntity<*> {
        return aggregatorService.addServiceProvider(serviceProviderDataModel)
    }

    @GetMapping("/doctor")
    fun fetchDoctorDetailsByToken(@RequestHeader(name="Authorization") doctorToken: String): ResponseEntity<*> {
        return doctorService.getDoctorDetails(doctorToken)
    }

    @GetMapping("/doctor/id")
    fun fetchDoctorDetailsById(@RequestHeader doctorId: String): ResponseEntity<*> {
        return ResponseEntity.ok(doctorService.getDoctorDetailsByUserId(doctorId))
    }

    @GetMapping("/driver")
    fun fetchDriverDetailsByToken(@RequestHeader(name="Authorization") driverToken: String): ResponseEntity<*> {
        return driverService.getDriverDetails(driverToken)
    }

    @GetMapping("/driver/id")
    fun fetchDriverDetailsById(@RequestHeader driverId: String): ResponseEntity<*> {
        return ResponseEntity.ok(driverService.getDriverDetailsByUserId(driverId))
    }

    @RolesAllowed("CUSTOMER")
    @PostMapping("patient/address")
    fun savePatientAddress(@RequestHeader (name="Authorization") userToken: String,
                           @RequestBody addressDetailsModel: AddressDetailsModel): ResponseEntity<*> {
        return addressService.saveAddress(addressDetailsModel, userToken)
    }

    @GetMapping("patient/address/id")
    fun getAddressByAddressId(@RequestHeader addressId: String): ResponseEntity<*> {
        return addressService.getAddressByAddressId(addressId)
    }

    @RolesAllowed("CUSTOMER")
    @GetMapping("patient/address")
    fun getAddressByToken(@RequestHeader (name="Authorization") userToken: String): ResponseEntity<*> {
        return addressService.getAllAddressByUserToken(userToken)
    }

    @RolesAllowed("CUSTOMER")
    @PostMapping("patient")
    fun addPatient(@RequestHeader (name="Authorization") userToken: String,
                   @RequestBody patientDetailsModel: PatientDetailsModel): ResponseEntity<*> {
        return userService.addPatient(patientDetailsModel, userToken)
    }

    @RolesAllowed("CUSTOMER")
    @GetMapping("/patient")
    fun getAllPatientWithToken(@RequestHeader (name="Authorization") userToken: String): ResponseEntity<*> {
        return userService.getAllPatientList(userToken)
    }

    @GetMapping("/patient/id")
    fun getPatientWithId(@RequestHeader id: String): ResponseEntity<*> {
        return userService.getPatientById(id)
    }
}