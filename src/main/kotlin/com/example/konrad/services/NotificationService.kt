package com.example.konrad.services


import com.example.konrad.constants.ApplicationConstants
import com.example.konrad.model.NotificationDetailsConvertor
import com.example.konrad.model.NotificationDetailsModel
import com.example.konrad.model.ResponseModel
import com.example.konrad.repositories.NotificationRepository
import com.example.konrad.repositories.UserDetailsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class NotificationService(
    @Autowired private val notificationRepository: NotificationRepository,
    @Autowired private val kafkaTemplate: KafkaTemplate<String, NotificationDetailsModel>,
    @Autowired private val userDetailsRepository: UserDetailsRepository
) {
    fun sendNotification(notificationDetailsModel: NotificationDetailsModel) {
        val fcmToken = getFcmToken(notificationDetailsModel.userId!!)
        fcmToken?.let {
            notificationDetailsModel.fcmToken = it
            kafkaTemplate.send("notificationTopic", notificationDetailsModel)
            notificationRepository.save(NotificationDetailsConvertor.toEntity(notificationDetailsModel))
        }
    }

    fun getAllNotificationByUserId(userId: String, page: Int?, pageSize: Int?): ResponseEntity<*> {
        val pageable: Pageable = PageRequest.of(
            (page ?: 1) - 1,
            pageSize ?: ApplicationConstants.PAGE_SIZE,
            Sort.by(Sort.Direction.DESC, "modifiedAt")
        )
        val notificationList = notificationRepository.findByUserId(userId, pageable)
        return ResponseEntity.ok().body(notificationList.map { NotificationDetailsConvertor.toModel(it) })
    }

    fun getFcmToken(userId: String): String? {
        val userDetails = userDetailsRepository.findByUsernameOrUserId(userId)
        if(userDetails.isPresent) {
            return userDetails.get().fcmToken
        }
        return null
    }

    fun saveFcmToken(userId: String?, fcmToken: String?): ResponseEntity<*> {
        userId?.let {
            val userDetailsResponse = userDetailsRepository.findByUsernameOrUserId(it)
            if(userDetailsResponse.isPresent) {
                val userDetails = userDetailsResponse.get()
                userDetails.fcmToken = fcmToken
                userDetailsRepository.save(userDetails)
                return ResponseEntity.ok(ResponseModel(success = true, body = null))
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseModel(success = false, body = null))
    }
}