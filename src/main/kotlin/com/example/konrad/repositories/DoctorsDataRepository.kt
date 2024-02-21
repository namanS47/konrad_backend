package com.example.konrad.repositories

import com.example.konrad.entity.DoctorDataEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.Optional

interface DoctorsDataRepository: MongoRepository<DoctorDataEntity, String> {
    @Query("{'\$or':[ {'username': ?0}, {'user_id': ?0} ] }")
    fun findByUsernameOrUserId(id: String): Optional<DoctorDataEntity>
    fun findAllByAssociatedSPId(spId: String): List<DoctorDataEntity>
}