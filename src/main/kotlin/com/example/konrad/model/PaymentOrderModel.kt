package com.example.konrad.model

import com.example.konrad.entity.PaymentOrderEntity

class PaymentOrderModel(
    var id: String? = null,
    var intentId: String? = null,
    var amount: Long? = null,
    var clientSecret: String? = null,
    var created: Long? = null,
    var currency: String? = null,
    var status: String? = null
)

object PaymentOrderConvertor {
    fun toEntity(paymentOrderModel: PaymentOrderModel): PaymentOrderEntity {
        val entity = PaymentOrderEntity()
        entity.apply {
            intentId = paymentOrderModel.intentId
            amount = paymentOrderModel.amount
            created = paymentOrderModel.created
            status = paymentOrderModel.status
            currency = paymentOrderModel.currency
        }
        return entity
    }

    fun toModel(paymentOrderEntity: PaymentOrderEntity): PaymentOrderModel {
        val model = PaymentOrderModel()
        model.apply {
            id = paymentOrderEntity.id
            intentId = paymentOrderEntity.intentId
            amount = paymentOrderEntity.amount
            created = paymentOrderEntity.created
            status = paymentOrderEntity.status
            currency = paymentOrderEntity.currency
        }
        return model
    }
}