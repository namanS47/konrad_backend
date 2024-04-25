package com.example.konrad.controller

import com.example.konrad.model.DoctorDataModel
import com.example.konrad.model.DriverDataModel
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
    fun fetchServiceProviderDetails(@RequestHeader(name = "Authorization") token: String): ResponseEntity<*> {
        return aggregatorService.getServiceProviderDetailsByToken(token)
    }

    @RolesAllowed("SERVICE_PROVIDER")
    @PostMapping("/doctor")
    fun addDoctor(
        @RequestHeader(name = "Authorization") spToken: String,
        @RequestBody doctorDataModel: DoctorDataModel
    ): ResponseEntity<*> {
        return aggregatorService.createDoctorOrNurse(doctorDataModel, spToken)
    }

    @RolesAllowed("SERVICE_PROVIDER")
    @GetMapping("/doctors")
    fun getAllDoctorsAssociatedWithSP(
        @RequestHeader(name = "Authorization") spToken: String, @RequestParam("type") type: String?,
        @RequestParam("page") page: Int = 1,
        @RequestParam("pageSize") pageSize: Int?,
    ): ResponseEntity<*> {
        return aggregatorService.getAllDoctorsAssociatedWithSP(spToken, page, pageSize, type)
    }

    @RolesAllowed("SERVICE_PROVIDER")
    @PostMapping("/driver")
    fun addDriver(
        @RequestHeader(name = "Authorization") spToken: String,
        @RequestBody driverDataModel: DriverDataModel
    ): ResponseEntity<*> {
        return aggregatorService.createDriverWithCredentials(driverDataModel, spToken)
    }

    @RolesAllowed("SERVICE_PROVIDER")
    @GetMapping("/drivers")
    fun getAllDriversAssociatedWithSP(@RequestHeader(name = "Authorization") spToken: String): ResponseEntity<*> {
        return aggregatorService.getAllDriversAssociatedWithSP(spToken)
    }
}