package com.example.konrad.repositories

import com.example.konrad.entity.NotificationDetailsEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface NotificationRepository : MongoRepository<NotificationDetailsEntity, String> {
    fun findByUserId(userId: String, pageable: Pageable): List<NotificationDetailsEntity>
}