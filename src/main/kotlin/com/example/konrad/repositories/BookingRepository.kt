package com.example.konrad.repositories

import com.example.konrad.entity.BookingDetailsEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface BookingRepository : MongoRepository<BookingDetailsEntity, String> {
    @Query(value = "{ 'aggregator_id' : ?0, 'current_status': {\$in: ?1}}")
    fun findAllByAggregatorIdAndFilter(
        aggregatorId: String,
        statusList: List<String>,
        pageable: Pageable
    ): List<BookingDetailsEntity>

    @Query(value = "{ 'aggregator_id' : ?0, 'current_status': {\$in: ?1}}", count = true)
    fun countByAggregatorIdAndFilter(aggregatorId: String, statusList: List<String>): Long

    fun findAllByPatientId(patientId: String): List<BookingDetailsEntity>

    fun findAllByDriverId(driverId: String): List<BookingDetailsEntity>

    @Query(value = "{ 'user_id' : ?0, 'current_status': {\$in: ?1}}")
    fun findAllByUserIdAndFilter(
        userId: String,
        statusList: List<String>,
        pageable: Pageable
    ): List<BookingDetailsEntity>

    fun findAllByUserId(userId: String, pageable: Pageable): List<BookingDetailsEntity>
}