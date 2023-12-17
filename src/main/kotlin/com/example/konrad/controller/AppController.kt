package com.example.konrad.controller

import com.example.konrad.model.DoctorDataModel
import com.example.konrad.model.ServiceProviderDataModel
import com.example.konrad.model.jwt_models.UserDetailsModel
import com.example.konrad.services.AggregatorService
import com.example.konrad.services.DistanceMatrixServices
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
        @Autowired val aggregatorService: AggregatorService
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

    @RolesAllowed("ADMIN", "SERVICE_PROVIDER")
    @GetMapping("/aggregator")
    fun fetchServiceProviderDetails(@RequestHeader (name="Authorization") token: String): ResponseEntity<*> {
        return aggregatorService.getServiceProviderDetailsByToken(token)
    }

    @RolesAllowed("SERVICE_PROVIDER")
    @PostMapping("/aggregator/doctor")
    fun addDoctor(@RequestHeader (name="Authorization") spToken: String, @RequestBody doctorDataModel: DoctorDataModel): ResponseEntity<*> {
        return aggregatorService.createDoctor(doctorDataModel, spToken)
    }
}