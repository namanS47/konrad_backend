package com.example.konrad.entity

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "patient_details")
class PatientDetailsEntity (
        var userId: String? = null,
        var name: String? = null,
        var age: Int? = null,
        var gender: String? = null,
        var language: String? = null,
): AppEntity()