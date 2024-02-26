package com.example.konrad.repositories

import com.example.konrad.entity.RefreshTokenEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.Optional

interface RefreshTokenRepository: MongoRepository<RefreshTokenEntity, String> {
    @Query("{'username': ?0, 'token_list': ?1 }")
    fun findByUsernameAndTokenListContaining(username: String, token: String): Optional<RefreshTokenEntity>
}