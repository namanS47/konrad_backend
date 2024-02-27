package com.example.konrad.repositories

import com.example.konrad.entity.PatientDetailsEntity
import com.example.konrad.model.PatientDetailsModel
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query


interface PatientRepository : MongoRepository<PatientDetailsEntity, String> {
    fun findAllByUserId(userId: String): List<PatientDetailsEntity>

    @Query("{'name': {\$regex: '^?0', \$options: 'i'}}")
    fun  findByStartingWithName(name: String?): List<PatientDetailsModel>
}