package com.example.konrad.repositories

import com.example.konrad.entity.FileUploadEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface FileDetailsRepository : MongoRepository<FileUploadEntity, String> {
    fun findAllByUserId(userId: String, pageable: Pageable): List<FileUploadEntity>
    fun findAllByPatientId(patientId: String, pageable: Pageable): List<FileUploadEntity>
    fun findAllByBookingId(patientId: String, pageable: Pageable): List<FileUploadEntity>
    @Query(value = "{ 'user_id' : ?0, 'file_type': {\$in: ?1}}")
    fun findAllByUserIdAndFileType(userId: String, fileType: List<String>, pageable: Pageable): List<FileUploadEntity>
    @Query(value = "{ 'patient_id' : ?0, 'file_type': {\$in: ?1}}")
    fun findAllByPatientIdAndFileType(patientId: String, fileType: List<String>, pageable: Pageable): List<FileUploadEntity>
    @Query(value = "{ 'booking_id' : ?0, 'file_type': {\$in: ?1}}")
    fun findAllByBookingIdAndFileType(bookingId: String, fileType: List<String>, pageable: Pageable): List<FileUploadEntity>
}