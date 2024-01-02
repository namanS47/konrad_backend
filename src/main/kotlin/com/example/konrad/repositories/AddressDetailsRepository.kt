package com.example.konrad.repositories

import com.example.konrad.entity.AddressDetailsEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface AddressDetailsRepository: MongoRepository<AddressDetailsEntity, String> {
    fun findAllByUserId(userid: String): List<AddressDetailsEntity>
}