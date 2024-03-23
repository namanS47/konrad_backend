package com.example.konrad.services


import com.example.konrad.config.jwt.JwtTokenUtil
import com.example.konrad.constants.ApplicationConstants
import com.example.konrad.entity.FcmTokenDetailsEntity
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
import java.util.*

@Service
class NotificationService(
    @Autowired private val notificationRepository: NotificationRepository,
    @Autowired private val kafkaTemplate: KafkaTemplate<String, NotificationDetailsModel>,
    @Autowired private val userDetailsRepository: UserDetailsRepository,
    @Autowired private val jwtTokenUtil: JwtTokenUtil,
) {
    fun sendNotification(notificationDetailsModel: NotificationDetailsModel) {
        val fcmToken = getFcmToken(notificationDetailsModel.userId!!)
        notificationRepository.save(NotificationDetailsConvertor.toEntity(notificationDetailsModel))
        fcmToken?.map {
            notificationDetailsModel.fcmToken = it.token
            kafkaTemplate.send("notificationTopic", notificationDetailsModel)
        }
    }

    fun getAllNotificationByUserId(userId: String, page: Int?, pageSize: Int?): ResponseEntity<*> {
        val pageable: Pageable = PageRequest.of(
            (page ?: 1) - 1,
            pageSize ?: ApplicationConstants.PAGE_SIZE,
            Sort.by(Sort.Direction.DESC, "modifiedAt")
        )
        val notificationList = notificationRepository.findByUserId(userId, pageable)
        return ResponseEntity.ok().body(
            ResponseModel(
                success = true,
                body = mapOf("notifications" to notificationList.map { NotificationDetailsConvertor.toModel(it) })
            )
        )
    }

    fun getFcmToken(userId: String): List<FcmTokenDetailsEntity>? {
        val userDetails = userDetailsRepository.findByUsernameOrUserId(userId)
        if (userDetails.isPresent) {
            return userDetails.get().fcmTokens
        }
        return null
    }

    fun saveFcmToken(authToken: String, fcmToken: String): ResponseEntity<*> {
        val userId = jwtTokenUtil.getUsernameFromToken(authToken)
        val userDetailsResponse = userDetailsRepository.findByUsernameOrUserId(userId)
        if (userDetailsResponse.isPresent) {
            val userDetails = userDetailsResponse.get()

            userDetails.fcmTokens?.let {
                var fcmAlreadyExist = false

                it.forEach { fcmDetails ->
                    if (fcmDetails.token == fcmToken) {
                        fcmAlreadyExist = true
                        fcmDetails.modifiedAt = Date()
                    }
                }
                if (!fcmAlreadyExist) {
                    it.add(FcmTokenDetailsEntity(token = fcmToken, createdAt = Date(), modifiedAt = Date()))
                }
            } ?: run {
                userDetails.fcmTokens =
                    mutableListOf(FcmTokenDetailsEntity(token = fcmToken, createdAt = Date(), modifiedAt = Date()))
            }

            userDetailsRepository.save(userDetails)
            return ResponseEntity.ok(ResponseModel(success = true, body = null))
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseModel(success = false, body = null))
    }

    fun deleteFcmToken(userId: String, fcmToken: String): ResponseEntity<*> {
        val userDetailsResponse = userDetailsRepository.findByUsernameOrUserId(userId)
        if(userDetailsResponse.isPresent) {
            val userDetails = userDetailsResponse.get()

            val fcmIndex = userDetails.fcmTokens?.indexOfFirst { it.token == fcmToken } ?: -1
            if(fcmIndex != -1) {
                userDetails.fcmTokens?.removeAt(fcmIndex)
            }
            userDetailsRepository.save(userDetails)
        }
        return ResponseEntity.ok(ResponseModel(success = true, body = null))
    }
}