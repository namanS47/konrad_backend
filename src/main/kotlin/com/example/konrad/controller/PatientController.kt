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

    @PostMapping("/address")
    fun savePatientAddress(
        @RequestHeader(name = "Authorization") userToken: String,
        @RequestHeader userId: String?,
        @RequestBody addressDetailsModel: AddressDetailsModel
    ): ResponseEntity<*> {
        return addressService.saveAddress(addressDetailsModel, userToken, userId)
    }

    @GetMapping("/address/id")
    fun getAddressByAddressId(@RequestHeader addressId: String): ResponseEntity<*> {
        return addressService.getAddressByAddressId(addressId)
    }


    @GetMapping("/address")
    fun getAddressByToken(
        @RequestHeader(name = "Authorization") userToken: String,
        @RequestHeader userId: String?,
    ): ResponseEntity<*> {
        return addressService.getAllAddressByUserToken(userToken, userId)
    }

    @PostMapping("")
    fun addPatient(
        @RequestHeader(name = "Authorization") userToken: String,
        @RequestHeader userId: String?,
        @RequestBody patientDetailsModel: PatientDetailsModel
    ): ResponseEntity<*> {
        return userService.addPatient(patientDetailsModel, userToken, userId)
    }


    @PutMapping("")
    fun editPatient(
        @RequestHeader(name = "Authorization") userToken: String,
        @RequestBody patientDetailsModel: PatientDetailsModel
    ): ResponseEntity<*> {
        return userService.editPatient(patientDetailsModel, userToken)
    }

    @GetMapping("")
    fun getAllPatientAssociatedWithUser(
        @RequestHeader(name = "Authorization") userToken: String,
        @RequestHeader userId: String?
    ): ResponseEntity<*> {
        return userService.getAllPatientList(userToken, userId)
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