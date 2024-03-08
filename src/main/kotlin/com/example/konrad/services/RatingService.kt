package com.example.konrad.services

import com.example.konrad.constants.ApplicationConstants
import com.example.konrad.entity.UserRatingEntity
import com.example.konrad.model.PatientDetailsObject
import com.example.konrad.model.ResponseModel
import com.example.konrad.model.UserRatingModel
import com.example.konrad.model.UserRatingObject
import com.example.konrad.repositories.PatientRepository
import com.example.konrad.repositories.UserRatingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class RatingService(
    @Autowired private val userRatingRepository: UserRatingRepository,
    @Autowired private val patientRepository: PatientRepository,
) {
    fun addRating(userRatingModel: UserRatingModel): ResponseEntity<*> {
        if (userRatingModel.rating == null && userRatingModel.review == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseModel(success = false, reason = "rating and review both can't be empty", body = null))
        }

        if (!userRatingModel.bookingId.isNullOrEmpty() &&
            userRatingRepository.findByBookingId(userRatingModel.bookingId!!).isPresent
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                    ResponseModel(
                        success = false,
                        reason = "rating is already received with this booking",
                        body = null
                    )
                )
        }

        userRatingRepository.save(UserRatingObject.toEntity(userRatingModel))
        return ResponseEntity.ok().body(ResponseModel(success = true, body = null))
    }

    fun getAllRatings(
        bookingId: String?,
        userId: String?,
        modelList: List<String>?,
        page: Int,
        pageSize: Int?
    ): ResponseEntity<*> {
        if (!bookingId.isNullOrEmpty()) {
            val ratingResponse = userRatingRepository.findByBookingId(bookingId)
            return if (ratingResponse.isPresent) {
                ResponseEntity.ok(
                    ResponseModel(
                        success = true,
                        body = aggregateAllDetailsInRatingModel(ratingResponse.get(), modelList)
                    )
                )
            } else {
                ResponseEntity.ok(ResponseModel(success = false, body = null))
            }
        }

        val pageable: Pageable = PageRequest.of(
            page - 1,
            pageSize ?: ApplicationConstants.PAGE_SIZE,
            Sort.by(Sort.Direction.DESC, "rating")
        )

        val ratingResponseList = userId
            ?.let {
                userRatingRepository.findAllByUserId(it)
            }
            ?: run {
                userRatingRepository.findAll(pageable).toList()
            }



        return ResponseEntity.ok(
            ResponseModel(
                success = true,
                body = mapOf("ratings" to ratingResponseList.map { aggregateAllDetailsInRatingModel(it, modelList) })
            )
        )
    }

    fun aggregateAllDetailsInRatingModel(
        userRatingEntity: UserRatingEntity,
        modelList: List<String>?
    ): UserRatingModel {
        val userRatingModel = UserRatingObject.toModel(userRatingEntity)
        if (modelList?.contains("patient_id") == true && !userRatingEntity.patientId.isNullOrEmpty()) {
            val patientDetails = patientRepository.findById(userRatingEntity.patientId!!)
            if (patientDetails.isPresent) {
                userRatingModel.patientDetails = PatientDetailsObject.toModel(patientDetails.get())
            }
        }
        return userRatingModel
    }
}