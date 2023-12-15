package com.example.konrad.repositories

import com.example.konrad.entity.DoctorDataEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface DoctorsDataRepository: MongoRepository<DoctorDataEntity, String> {
    fun findByUserId(userId: String): Optional<DoctorDataEntity>
}