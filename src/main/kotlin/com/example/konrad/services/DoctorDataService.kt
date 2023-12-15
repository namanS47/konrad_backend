package com.example.konrad.services

import com.example.konrad.model.DoctorDataModel
import com.example.konrad.repositories.DoctorsDataRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DoctorDataService(
        @Autowired val doctorsDataRepository: DoctorsDataRepository
) {
    fun addDoctor(doctorDataModel: DoctorDataModel) {

    }
}