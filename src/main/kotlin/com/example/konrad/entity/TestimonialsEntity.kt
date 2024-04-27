package com.example.konrad.entity

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "testimonials")
class TestimonialsEntity(
    var rating: Double? = null,
    var review: String? = null,
    @Field("user_title")
    var userTitle: String? = null,
    var name: String? = null,
) : AppEntity()