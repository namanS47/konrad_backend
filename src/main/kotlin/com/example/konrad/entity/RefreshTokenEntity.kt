package com.example.konrad.entity

import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.Instant

@Document(collection = "refresh_token")
class RefreshTokenEntity (
    @Indexed
    var username: String? = null,
    @Field("expiry_date")
    var expiryDate: Instant? = null,
    @Field("token_list")
    var tokenList: MutableList<String>? = null,
): AppEntity()