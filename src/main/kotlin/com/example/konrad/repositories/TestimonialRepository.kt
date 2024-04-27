package com.example.konrad.repositories

import com.example.konrad.entity.TestimonialsEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface TestimonialRepository : MongoRepository<TestimonialsEntity, String>