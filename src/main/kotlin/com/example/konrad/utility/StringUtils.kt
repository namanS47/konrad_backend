package com.example.konrad.utility

import java.util.UUID

object StringUtils {
    fun isNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() }
    }

    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}