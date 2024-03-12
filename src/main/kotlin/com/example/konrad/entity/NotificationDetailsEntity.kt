package com.example.konrad.entity

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import kotlin.collections.HashMap

@Document(collection = "notifications")
class NotificationDetailsEntity (
        @Field("user_id")
        var userId: String? = null,

        var title: String? = null,

        var body: String? = null,

        var data: HashMap<String, String> = hashMapOf(),
): AppEntity()