package com.example.konrad.controller

import com.example.konrad.model.DoctorDataModel
import com.example.konrad.model.ServiceProviderDataModel
import com.example.konrad.model.jwt_models.UserDetailsModel
import com.example.konrad.services.AggregatorService
import com.example.konrad.services.DistanceMatrixServices
import com.example.konrad.services.DoctorService
import com.example.konrad.services.jwtService.JwtUserDetailsService
import jakarta.annotation.security.RolesAllowed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class AppController(
        @Autowired val distanceMatrixServices: DistanceMatrixServices,
        @Autowired val jwtUserDetailsService: JwtUserDetailsService,
        @Autowired val aggregatorService: AggregatorService,
        @Autowired val doctorService: DoctorService
) {
    @GetMapping("/")
    fun runDistanceMatrix() {
        distanceMatrixServices.getDistance()
    }

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

    @RolesAllowed("DOCTOR")
    @GetMapping("/doctor")
    fun fetchDoctorDetailsByToken(@RequestHeader(name="Authorization") doctorToken: String): ResponseEntity<*> {
        return doctorService.getDoctorDetails(doctorToken)
    }

    @GetMapping("/doctor/id")
    fun fetchDoctorDetailsById(@RequestHeader doctorId: String): ResponseEntity<*> {
        return ResponseEntity.ok(doctorService.getDoctorDetailsByUserId(doctorId))
    }
}