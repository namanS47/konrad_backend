package com.example.konrad.controller

import com.example.konrad.model.DoctorDataModel
import com.example.konrad.services.AggregatorService
import jakarta.annotation.security.RolesAllowed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/aggregator")
class AggregatorController(
        @Autowired val aggregatorService: AggregatorService
) {
    @RolesAllowed("ADMIN", "SERVICE_PROVIDER")
    @GetMapping
    fun fetchServiceProviderDetails(@RequestHeader(name="Authorization") token: String): ResponseEntity<*> {
        return aggregatorService.getServiceProviderDetailsByToken(token)
    }

    @RolesAllowed("SERVICE_PROVIDER")
    @PostMapping("/doctor")
    fun addDoctor(@RequestHeader(name="Authorization") spToken: String, @RequestBody doctorDataModel: DoctorDataModel): ResponseEntity<*> {
        return aggregatorService.createDoctor(doctorDataModel, spToken)
    }
}