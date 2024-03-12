package com.example.konrad.model

import com.example.konrad.entity.NotificationDetailsEntity
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.util.Date

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class NotificationDetailsModel (
    var userId: String? = null,
    var userIds: List<String>? = null,
    var title: String? = null,
    var body: String? = null,
    var data: HashMap<String, String> = hashMapOf(),
    var date: Date? = null,
    var fcmToken: String? = null,
)

object NotificationDetailsConvertor {
    fun toEntity(notificationDetailsModel: NotificationDetailsModel) : NotificationDetailsEntity {
        val entity = NotificationDetailsEntity()
        entity.apply {
            userId = notificationDetailsModel.userId
            title = notificationDetailsModel.title
            body = notificationDetailsModel.body
            data = notificationDetailsModel.data
        }
        return entity
    }

    fun toModel(notificationDetailsEntity: NotificationDetailsEntity): NotificationDetailsModel {
        val model = NotificationDetailsModel()
        model.apply {
            userId = notificationDetailsEntity.userId
            title = notificationDetailsEntity.title
            body = notificationDetailsEntity.body
            data = notificationDetailsEntity.data
            date = notificationDetailsEntity.createdAt
        }
        return model
    }
}