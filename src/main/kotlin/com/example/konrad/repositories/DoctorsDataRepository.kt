package com.example.konrad.repositories

import com.example.konrad.entity.DoctorDataEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface DoctorsDataRepository: MongoRepository<DoctorDataEntity, String> {
    fun findByUsername(username: String): Optional<DoctorDataEntity>
    fun findAllByAssociatedSPId(spId: String): List<DoctorDataEntity>
}