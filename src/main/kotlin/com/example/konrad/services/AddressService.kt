package com.example.konrad.services

import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.model.AddressDetailsConvertor
import com.example.konrad.model.AddressDetailsModel
import com.example.konrad.model.ResponseModel
import com.example.konrad.repositories.AddressDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class AddressService(
        @Autowired private val addressDetailsRepository: AddressDetailsRepository,
        @Autowired private val jwtTokenUtil: JwtTokenUtil
) {
    fun saveAddress(addressDetailsModel: AddressDetailsModel, token: String): ResponseEntity<*> {
        if(!addressDetailsModel.id.isNullOrEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseModel(success = false,
                    reason = "please remove id", body = null))
        }

        val username = jwtTokenUtil.getUsernameFromToken(token)
        addressDetailsModel.userId = username
        val isAddressValidResponse = AddressDetailsConvertor.checkAddressValid(addressDetailsModel)
        if (isAddressValidResponse.success != true) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(isAddressValidResponse)
        }

        return try {
            addressDetailsRepository.save(AddressDetailsConvertor.toEntity(addressDetailsModel))
            ResponseEntity.ok(ResponseModel(success = true, body = null))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseModel(success = false, reason = e.message, body = null))
        }
    }

    fun getAllAddressByUserToken(token: String): ResponseEntity<*> {
        val username = jwtTokenUtil.getUsernameFromToken(token)
        val addressList = addressDetailsRepository.findAllByUserId(username)
        return ResponseEntity.ok(ResponseModel(success = true, body = addressList.map { AddressDetailsConvertor.toModel(it) }))
    }

    fun getAddressByAddressId(addressId: String): ResponseEntity<*> {
        val response = addressDetailsRepository.findById(addressId)
        return if (response.isPresent) {
            ResponseEntity.ok(ResponseModel(success = true, body = AddressDetailsConvertor.toModel(response.get())))
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseModel(success = false, body = null, reason = "No address found with this address id"))
        }
    }
}