package com.example.konrad.controller

import com.example.konrad.model.AddressDetailsModel
import com.example.konrad.model.PatientDetailsModel
import com.example.konrad.services.AddressService
import com.example.konrad.services.UserService
import jakarta.annotation.security.RolesAllowed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/patient")
class PatientController(
    @Autowired private val addressService: AddressService,
    @Autowired private val userService: UserService,
) {
    @RolesAllowed("CUSTOMER")
    @PostMapping("/address")
    fun savePatientAddress(
        @RequestHeader(name = "Authorization") userToken: String,
        @RequestBody addressDetailsModel: AddressDetailsModel
    ): ResponseEntity<*> {
        return addressService.saveAddress(addressDetailsModel, userToken)
    }

    @GetMapping("/address/id")
    fun getAddressByAddressId(@RequestHeader addressId: String): ResponseEntity<*> {
        return addressService.getAddressByAddressId(addressId)
    }

    @RolesAllowed("CUSTOMER")
    @GetMapping("/address")
    fun getAddressByToken(@RequestHeader(name = "Authorization") userToken: String): ResponseEntity<*> {
        return addressService.getAllAddressByUserToken(userToken)
    }

    @RolesAllowed("CUSTOMER")
    @PostMapping("")
    fun addPatient(
        @RequestHeader(name = "Authorization") userToken: String,
        @RequestBody patientDetailsModel: PatientDetailsModel
    ): ResponseEntity<*> {
        return userService.addPatient(patientDetailsModel, userToken)
    }

    @RolesAllowed("CUSTOMER")
    @PutMapping("")
    fun editPatient(
        @RequestHeader(name = "Authorization") userToken: String,
        @RequestBody patientDetailsModel: PatientDetailsModel
    ): ResponseEntity<*> {
        return userService.editPatient(patientDetailsModel, userToken)
    }

    @RolesAllowed("CUSTOMER")
    @GetMapping("")
    fun getAllPatientWithToken(@RequestHeader(name = "Authorization") userToken: String): ResponseEntity<*> {
        return userService.getAllPatientList(userToken)
    }

    @GetMapping("/id")
    fun getPatientWithId(@RequestHeader id: String): ResponseEntity<*> {
        return userService.getPatientById(id)
    }

    @RolesAllowed("CUSTOMER")
    @PostMapping("/profilePicture")
    fun addProfilePicture(@RequestPart("file") file: MultipartFile, @RequestHeader id: String): ResponseEntity<*> {
        return userService.addPatientProfilePicture(file, id)
    }

    @GetMapping("/profile")
    fun fetchUserPatientProfile(@RequestHeader(name = "Authorization") userToken: String): ResponseEntity<*> {
        return userService.fetchUserPatientProfile(userToken)
    }
}