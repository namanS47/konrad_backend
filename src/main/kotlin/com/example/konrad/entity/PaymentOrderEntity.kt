package com.example.konrad.entity

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "payment_orders")
class PaymentOrderEntity (
    @Field("intent_id")
    var intentId: String? = null,
    var amount: Long? = null,
    var currency: String? = null,
    var created: Long? = null,
    var status: String? = null
) : AppEntity()