package com.example.konrad.repositories

import com.example.konrad.entity.DriverDataEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.*

interface DriverDataRepository: MongoRepository<DriverDataEntity, String> {
    @Query("{'\$or':[ {'username': ?0}, {'user_id': ?0} ] }")
    fun findByUsernameOrUserId(id: String): Optional<DriverDataEntity>
    fun findAllByAssociatedSPId(spId: String): List<DriverDataEntity>
}