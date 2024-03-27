package com.example.konrad.repositories

import com.example.konrad.entity.PaymentOrderEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface PaymentOrderRepository : MongoRepository<PaymentOrderEntity, String> {
    fun findByIntentId(intentId: String): Optional<PaymentOrderEntity>
}