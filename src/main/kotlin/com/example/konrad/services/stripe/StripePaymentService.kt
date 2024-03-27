package com.example.konrad.services.stripe

import com.example.konrad.model.PaymentOrderConvertor
import com.example.konrad.model.PaymentOrderModel
import com.example.konrad.model.ResponseModel
import com.example.konrad.repositories.PaymentOrderRepository
import com.google.gson.JsonSyntaxException
import com.stripe.Stripe
import com.stripe.exception.SignatureVerificationException
import com.stripe.model.Event
import com.stripe.model.PaymentIntent
import com.stripe.net.Webhook
import com.stripe.param.PaymentIntentCreateParams
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service


@Service
class StripePaymentService(
    @Autowired private val orderRepository: PaymentOrderRepository
) {
    @Value("\${stripe.secret-key}")
    private lateinit var apiKey: String

    @Value("\${stripe.webhook.secret}")
    private lateinit var endpointSecret: String

    fun createPaymentIntent(paymentIntent: PaymentOrderModel): ResponseEntity<*> {
        try {
            Stripe.apiKey = apiKey
            val params = PaymentIntentCreateParams.builder()
                .setAmount(paymentIntent.amount?.toLong())
                .setCurrency(paymentIntent.currency)
                .build()

            val createdPaymentIntent = PaymentIntent.create(params)
            val paymentOrderModel = PaymentOrderModel(
                intentId = createdPaymentIntent.id,
                clientSecret = createdPaymentIntent.clientSecret,
                currency = createdPaymentIntent.currency,
                amount = createdPaymentIntent.amount,
                created = createdPaymentIntent.created,
            )
            orderRepository.save(PaymentOrderConvertor.toEntity(paymentOrderModel))
            return ResponseEntity.ok(ResponseModel(success = true, body = paymentOrderModel))
        } catch (e: Exception) {
            throw (e)
        }
    }

    fun paymentEventsListener(stripeBody: String, sigHeader: String): ResponseEntity<*> {
        Stripe.apiKey = apiKey
        val event: Event?
        try {
            event = Webhook.constructEvent(
                stripeBody, sigHeader, endpointSecret
            )
        } catch (e: JsonSyntaxException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload")
        } catch (e: SignatureVerificationException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature")
        }

        val intent = event!!
            .dataObjectDeserializer
            .getObject()
            .get() as PaymentIntent

        val orderIntentResponse = orderRepository.findByIntentId(intent.id)
        if (orderIntentResponse.isPresent) {
            val orderIntent = orderIntentResponse.get()
            orderIntent.status = intent.status
            orderRepository.save(orderIntent)
        } else {
            val orderIntent = PaymentOrderModel(
                intentId = intent.id,
                status = intent.status,
                amount = intent.amount,
                created = intent.created,
                currency = intent.currency
            )
            orderRepository.save(PaymentOrderConvertor.toEntity(orderIntent))
        }
        return ResponseEntity.ok(null)
    }

    fun getPaymentStatus(intentId: String): ResponseEntity<*> {
        Stripe.apiKey = apiKey

        val orderIntentResponse = orderRepository.findByIntentId(intentId)
        if (orderIntentResponse.isPresent) {
            val orderIntent = orderIntentResponse.get()

            if (orderIntent.status == "succeeded" || orderIntent.status == "payment_failed"
                || orderIntent.status == "canceled"
            ) {
                return ResponseEntity.ok(
                    ResponseModel(
                        success = true,
                        body = PaymentOrderConvertor.toModel(orderIntent)
                    )
                )
            }

            val paymentIntent = PaymentIntent.retrieve(intentId)
            orderIntent.status = paymentIntent.status
            orderRepository.save(orderIntent)
            return ResponseEntity.ok(
                ResponseModel(success = true, body = PaymentOrderConvertor.toModel(orderIntent))
            )

        } else {
            val intent = PaymentIntent.retrieve(intentId)
            val orderIntent = PaymentOrderModel(
                intentId = intent.id,
                status = intent.status,
                amount = intent.amount,
                created = intent.created,
                currency = intent.currency
            )
            orderRepository.save(PaymentOrderConvertor.toEntity(orderIntent))

            return ResponseEntity.ok(
                ResponseModel(success = true, body = orderIntent)
            )
        }
    }
}