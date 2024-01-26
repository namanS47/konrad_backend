package com.example.konrad.repositories

import com.example.konrad.entity.BookingDetailsEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface BookingRepository: MongoRepository<BookingDetailsEntity, String> {
    fun findAllByAggregatorId(aggregatorId: String): List<BookingDetailsEntity>
    fun findAllByDriverId(driverId: String): List<BookingDetailsEntity>

    fun findAllByUserId(userId: String): List<BookingDetailsEntity>
}