package com.example.konrad.entity

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "patient_details")
class PatientDetailsEntity (
        var userId: String? = null,
        var name: String? = null,
        var age: Int? = null,
        var gender: String? = null,
        var email: String? = null,
        var profilePictureFileName: String? = null,
        var mobileNumber: String? = null,
        var countryCode: String? = null,
        var relationShip: String? = null,
        var language: String? = null,
): AppEntity()