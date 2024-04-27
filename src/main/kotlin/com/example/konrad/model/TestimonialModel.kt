package com.example.konrad.model

import com.example.konrad.entity.TestimonialsEntity
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class TestimonialModel(
    var id: String? = null,
    var rating: Double? = null,
    var review: String? = null,
    var userTitle: String? = null,
    var name: String? = null,
)

object TestimonialsConvertor {
    fun toEntity(testimonialModel: TestimonialModel, testimonialsEntity: TestimonialsEntity?): TestimonialsEntity {
        val entity = testimonialsEntity ?: TestimonialsEntity()
        entity.apply {
            testimonialModel.rating?.let {
                rating = it
            }
            testimonialModel.review?.let {
                review = it
            }
            testimonialModel.userTitle?.let {
                userTitle = it
            }
            testimonialModel.name?.let {
                name = it
            }
        }
        return entity
    }

    fun toModel(testimonialsEntity: TestimonialsEntity): TestimonialModel {
        val model = TestimonialModel()
        model.apply {
            id = testimonialsEntity.id
            rating = testimonialsEntity.rating
            review = testimonialsEntity.review
            userTitle = testimonialsEntity.userTitle
            name = testimonialsEntity.name
        }
        return model
    }
}