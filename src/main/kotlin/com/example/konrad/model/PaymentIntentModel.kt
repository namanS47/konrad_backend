package com.example.konrad.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.google.gson.annotations.SerializedName

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PaymentIntentModel (
    @SerializedName("id"                                   ) var id                                : String?                  = null,
    @SerializedName("object"                               ) var objectString                      : String?                  = null,
    @SerializedName("amount"                               ) var amount                            : Long?                     = null,
    @SerializedName("amount_capturable"                    ) var amountCapturable                  : Int?                     = null,
    @SerializedName("amount_received"                      ) var amountReceived                    : Int?                     = null,
    @SerializedName("application_fee_amount"               ) var applicationFeeAmount              : Int?                  = null,
    @SerializedName("automatic_payment_methods"            ) var automaticPaymentMethods           : AutomaticPaymentMethods? = null,
    @SerializedName("canceled_at"                          ) var canceledAt                        : Long?                  = null,
    @SerializedName("cancellation_reason"                  ) var cancellationReason                : String?                  = null,
    @SerializedName("capture_method"                       ) var captureMethod                     : String?                  = null,
    @SerializedName("client_secret"                        ) var clientSecret                      : String?                  = null,
    @SerializedName("confirmation_method"                  ) var confirmationMethod                : String?                  = null,
    @SerializedName("created"                              ) var created                           : Long?                     = null,
    @SerializedName("currency"                             ) var currency                          : String?                  = null,
    @SerializedName("customer"                             ) var customer                          : String?                  = null,
    @SerializedName("description"                          ) var description                       : String?                  = null,
//    @SerializedName("invoice"                              ) var invoice                           : String?                  = null,
    @SerializedName("last_payment_error"                   ) var lastPaymentError                  : String?                  = null,
    @SerializedName("latest_charge"                        ) var latestCharge                      : String?                  = null,
    @SerializedName("livemode"                             ) var livemode                          : Boolean?                 = null,
    @SerializedName("metadata"                             ) var metadata                          : Map<String, String>?      = null,
    @SerializedName("next_action"                          ) var nextAction                        : String?                  = null,
    @SerializedName("on_behalf_of"                         ) var onBehalfOf                        : String?                  = null,
//    @SerializedName("payment_method"                       ) var paymentMethod                     : String?                  = null,
//    @SerializedName("payment_method_configuration_details" ) var paymentMethodConfigurationDetails : String?                  = null,
//    @SerializedName("payment_method_options"               ) var paymentMethodOptions              : PaymentMethodOptions?    = PaymentMethodOptions(),
//    @SerializedName("payment_method_types"                 ) var paymentMethodTypes                : ArrayList<String>?       = null,
    @SerializedName("processing"                           ) var processing                        : String?                  = null,
    @SerializedName("receipt_email"                        ) var receiptEmail                      : String?                  = null,
    @SerializedName("review"                               ) var review                            : String?                  = null,
    @SerializedName("setup_future_usage"                   ) var setupFutureUsage                  : String?                  = null,
    @SerializedName("shipping"                             ) var shipping                          : String?                  = null,
    @SerializedName("source"                               ) var source                            : String?                  = null,
    @SerializedName("statement_descriptor"                 ) var statementDescriptor               : String?                  = null,
    @SerializedName("statement_descriptor_suffix"          ) var statementDescriptorSuffix         : String?                  = null,
    @SerializedName("status"                               ) var status                            : String?                  = null,
    @SerializedName("transfer_data"                        ) var transferData                      : String?                  = null,
    @SerializedName("transfer_group"                       ) var transferGroup                     : String?                  = null
)

data class AutomaticPaymentMethods (
    @SerializedName("allow_redirects" ) var allowRedirects : String?  = null,
    @SerializedName("enabled"         ) var enabled        : Boolean? = null
)