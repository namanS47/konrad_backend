package com.example.konrad.config

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AmazonConfig {
    @Value("\${aws_access_key}")
    private lateinit var awsAccessKey: String

    @Value("\${aws_secret_key}")
    private lateinit var awsSecretKey: String

    @Bean
    fun s3(): AmazonS3 {
        val awsCredentials: AWSCredentials = BasicAWSCredentials(awsAccessKey, awsSecretKey)
        return AmazonS3ClientBuilder
                .standard()
                .withRegion("me-central-1")
                .withCredentials(AWSStaticCredentialsProvider(awsCredentials))
                .build()
    }
}