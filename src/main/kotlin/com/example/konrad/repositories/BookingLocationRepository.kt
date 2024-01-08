package com.example.konrad.repositories

import com.example.konrad.entity.BookingLocationEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface BookingLocationRepository: MongoRepository<BookingLocationEntity, String> {
    fun findByBookingId(bookingId: String): Optional<BookingLocationEntity>
}