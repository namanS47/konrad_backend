package com.example.konrad.controller

import com.example.konrad.model.TestimonialModel
import com.example.konrad.model.UserRatingModel
import com.example.konrad.services.RatingService
import jakarta.annotation.security.RolesAllowed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class RatingController(
    @Autowired private val ratingService: RatingService,
) {
    @PostMapping("/rating")
    fun addRating(@RequestBody ratingModel: UserRatingModel): ResponseEntity<*> {
        return ratingService.addRating(ratingModel)
    }

    @GetMapping("/rating")
    fun getRatings(
        @RequestHeader("bookingId") bookingId: String?,
        @RequestHeader("userId") userId: String?,
        @RequestParam("page") page: Int = 1,
        @RequestParam("pageSize") pageSize: Int?,
        @RequestParam("modelList") modelList: List<String>?
    ): ResponseEntity<*> {
        return ratingService.getAllRatings(bookingId, userId, modelList, page, pageSize)
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/testimonial")
    fun addTestimonial(@RequestBody testimonialModel: TestimonialModel): ResponseEntity<*> {
        return ratingService.addTestimonials(testimonialModel)
    }

    @RolesAllowed("ADMIN")
    @PutMapping("/testimonial")
    fun updateTestimonial(@RequestBody testimonialModel: TestimonialModel): ResponseEntity<*> {
        return ratingService.updateTestimonials(testimonialModel)
    }

    @GetMapping("/testimonials")
    fun getTestimonials(
        @RequestParam("page") page: Int = 1,
        @RequestParam("pageSize") pageSize: Int?,
        @RequestParam("sortBy") sortBy: String = "rating"
    ): ResponseEntity<*> {
        return ratingService.getTestimonials(page, pageSize, sortBy)
    }

    @RolesAllowed("ADMIN")
    @DeleteMapping("/testimonial")
    fun deleteTestimonial(@RequestHeader testimonialId: String): ResponseEntity<*> {
        return ratingService.deleteTestimonial(testimonialId)
    }
}