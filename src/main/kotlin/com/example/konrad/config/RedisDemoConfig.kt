package com.example.konrad.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import java.time.Duration


@Configuration
class RedisDemoConfig {
    @Value(value = "\${spring.redis.host}")
    private val host: String? = null

    @Value(value = "\${spring.redis.port}")
    private val port: String? = null

    @Value(value = "\${redis.timeout}")
    private val timeout: String? = null

    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
        redisStandaloneConfiguration.hostName = host!!
        redisStandaloneConfiguration.port = port!!.toInt()
        val jedisClientConfiguration = JedisClientConfiguration.builder()
        jedisClientConfiguration.connectTimeout(Duration.ofSeconds(timeout!!.toInt().toLong())) // connection timeout
        return JedisConnectionFactory(redisStandaloneConfiguration,
                jedisClientConfiguration.build())
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = jedisConnectionFactory()
        template.valueSerializer = GenericJackson2JsonRedisSerializer()
        return template
    }
}