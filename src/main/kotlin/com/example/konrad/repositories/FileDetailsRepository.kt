package com.example.konrad.repositories

import com.example.konrad.entity.FileUploadEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface FileDetailsRepository : MongoRepository<FileUploadEntity, String> {
    fun findAllByUserId(userId: String): List<FileUploadEntity>
    fun findAllByPatientId(patientId: String): List<FileUploadEntity>
    fun findAllByBookingId(patientId: String): List<FileUploadEntity>
    fun findAllByUserIdAndFileType(userId: String, fileType: String): List<FileUploadEntity>
    fun findAllByPatientIdAndFileType(patientId: String, fileType: String): List<FileUploadEntity>
    fun findAllByBookingIdAndFileType(bookingId: String, fileType: String): List<FileUploadEntity>
}