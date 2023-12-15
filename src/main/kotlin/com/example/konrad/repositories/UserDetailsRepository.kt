package com.example.konrad.repositories

import com.example.konrad.entity.UserDetailsEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface UserDetailsRepository: MongoRepository<UserDetailsEntity, String> {
    fun findByUsername(username: String): Optional<UserDetailsEntity>
}