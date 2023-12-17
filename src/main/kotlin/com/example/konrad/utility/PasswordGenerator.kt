package com.example.konrad.utility

import java.security.SecureRandom

object PasswordGenerator {
    fun generateSecurePassword(): String {
        val charset = ('A'..'Z') + ('a'..'z') + ('0'..'9') + listOf('!', '@', '#', '$', '%', '^', '&', '*', '(', ')')
        val secureRandom = SecureRandom()
        return (1..10)
                .map { charset[secureRandom.nextInt(charset.size)] }
                .joinToString("")
    }
}