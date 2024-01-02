package com.example.konrad.entity

import com.example.konrad.model.LatLong
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "address_details")
class AddressDetailsEntity(
        @Field(name = "user_id")
        var userId: String? = null,
        @Field(name = "address_one")
        var addressOne: String? = null,
        @Field(name = "address_two")
        var addressTwo: String? = null,
        var landmark: String? = null,
        var type: String? = null,
        @Field(name = "lat_long")
        var latLong: LatLong? = null,
        var description: String? = null,
): AppEntity()