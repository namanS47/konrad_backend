package com.example.konrad.repositories

import com.example.konrad.entity.UserDetailsEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.Optional

interface UserDetailsRepository: MongoRepository<UserDetailsEntity, String> {
    @Query("{'\$or':[ {'username': ?0}, {'user_id': ?0} ] }")
    fun findByUsernameOrUserId(username: String): Optional<UserDetailsEntity>
    fun findByMobileNumberAndCountryCode(mobileNumber: String, countryCode: String): Optional<UserDetailsEntity>
}