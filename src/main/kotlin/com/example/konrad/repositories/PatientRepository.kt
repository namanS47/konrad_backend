package com.example.konrad.repositories

import com.example.konrad.entity.PatientDetailsEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface PatientRepository : MongoRepository<PatientDetailsEntity, String> {
    fun findAllByUserId(userId: String): List<PatientDetailsEntity>
}