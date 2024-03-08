package com.example.konrad.model

import com.example.konrad.entity.UserRatingEntity
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class UserRatingModel(
    var id: String? = null,
    var userId: String? = null,
    var aggregatorId: String? = null,
    var patientId: String? = null,
    var bookingId: String? = null,
    var patientDetails: PatientDetailsModel? = null,
    var doctorId: String? = null,
    var nurseId: String? = null,
    var rating: Double? = null,
    var review: String? = null,
)

object UserRatingObject {
    fun toEntity(userRatingModel: UserRatingModel): UserRatingEntity {
        val entity = UserRatingEntity()
        entity.apply {
            userId = userRatingModel.userId
            aggregatorId = userRatingModel.aggregatorId
            patientId = userRatingModel.patientId
            bookingId = userRatingModel.bookingId
            doctorId = userRatingModel.patientId
            nurseId = userRatingModel.nurseId
            rating = userRatingModel.rating
            review = userRatingModel.review
        }
        return entity
    }

    fun toModel(userRatingEntity: UserRatingEntity): UserRatingModel {
        val model = UserRatingModel()
        model.apply {
            id = userRatingEntity.id
            userId = userRatingEntity.userId
            aggregatorId = userRatingEntity.aggregatorId
            patientId = userRatingEntity.patientId
            bookingId = userRatingEntity.bookingId
            doctorId = userRatingEntity.patientId
            nurseId = userRatingEntity.nurseId
            rating = userRatingEntity.rating
            review = userRatingEntity.review
        }
        return model
    }
}