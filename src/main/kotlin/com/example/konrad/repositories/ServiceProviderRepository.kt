package com.example.konrad.repositories

import com.example.konrad.entity.ServiceProviderEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface ServiceProviderRepository: MongoRepository<ServiceProviderEntity, String> {
    fun findByUserId(userId: String): Optional<ServiceProviderEntity>
    fun findByUsername(username: String): Optional<ServiceProviderEntity>
}