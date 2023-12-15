package com.example.konrad.controller

import com.example.konrad.model.jwt_models.UserDetailsModel
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
        @Autowired val jwtUserDetailsService: JwtUserDetailsService
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


}