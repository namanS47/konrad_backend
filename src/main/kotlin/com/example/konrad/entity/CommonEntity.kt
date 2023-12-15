package com.example.konrad.entity

import org.springframework.data.annotation.*
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

interface BaseEntity<ID> {
        var id: ID?
}

abstract class BaseStringIdEntity(
        @Id
        override var id: String? = null
) : BaseEntity<String>
abstract class AppEntity(
        @CreatedDate
        var createdAt: Date? = null,

        @LastModifiedDate
        @Field(name = "modified_at")
        //TODO: @ER Mark::ZonedDateTime
        var modifiedAt: Date? = null,

        @CreatedBy
        @Field(name = "created_by")
        var createdBy: String? = null,

        @LastModifiedBy
        @Field(name = "modified_by")
        var modifiedBy: String? = null,

        @Field(name = "is_deleted")
        var isDeleted: Boolean = false
) : BaseStringIdEntity()
