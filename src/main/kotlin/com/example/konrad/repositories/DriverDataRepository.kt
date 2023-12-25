package com.example.konrad.repositories

import com.example.konrad.entity.DriverDataEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface DriverDataRepository: MongoRepository<DriverDataEntity, String> {
    fun findByUsername(username: String): Optional<DriverDataEntity>
    fun findAllByAssociatedSPId(spId: String): List<DriverDataEntity>
}