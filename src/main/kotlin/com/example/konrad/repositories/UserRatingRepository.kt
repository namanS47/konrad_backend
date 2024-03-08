package com.example.konrad.repositories

import com.example.konrad.entity.UserRatingEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface UserRatingRepository : MongoRepository<UserRatingEntity, String> {
    fun findAllByUserId(userId: String): List<UserRatingEntity>
    fun findByBookingId(bookingId: String): Optional<UserRatingEntity>
}