package com.example.konrad.repositories

import com.example.konrad.entity.BookingDetailsEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.util.Optional

interface BookingRepository: MongoRepository<BookingDetailsEntity, String> {
    @Query(value = "{ 'aggregator_id' : ?0, 'current_status': {\$in: ?1}}")
    fun findAllByAggregatorIdAndFilter(aggregatorId: String, statusList: List<String>, pageable: Pageable): List<BookingDetailsEntity>

    fun findAllByDriverId(driverId: String): List<BookingDetailsEntity>

    fun findAllByUserId(userId: String): List<BookingDetailsEntity>
}