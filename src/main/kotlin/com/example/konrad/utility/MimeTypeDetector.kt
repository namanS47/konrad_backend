package com.example.konrad.utility

import jakarta.annotation.PostConstruct
import jdk.incubator.vector.VectorOperators.LOG
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.util.FileCopyUtils
import java.io.IOException
import java.nio.charset.StandardCharsets


@Component
class MimeTypeDetector private constructor() {

    private val log = LoggerFactory.getLogger(MimeTypeDetector::class.java)
    private var extensionToMimeTypeMapping: MutableMap<String, String> = mutableMapOf()
    private var mimeTypeToExtensionMapping: MutableMap<String, String> = mutableMapOf()

    companion object {

        private var detector: MimeTypeDetector? = null

        fun getInstance(): MimeTypeDetector {
            if (detector == null) {
                detector = MimeTypeDetector()
            }
            return detector!!
        }
    }

    @PostConstruct
    fun configure() {
        val supportedMimeTypes: MutableCollection<Pair<String, String>> = mutableListOf()
        val resource = ClassPathResource("mime-type-catalog.csv")
        assert(resource.exists())

        try {
            val catalog = FileCopyUtils.copyToByteArray(resource.inputStream)
            val catalogString = String(catalog, StandardCharsets.UTF_8)
            val mimeTypeList = catalogString.split("\n")
            mimeTypeList.forEach {
                val tokens = it.split(",")
                supportedMimeTypes.add(Pair(tokens.first(), tokens.last()))
            }
            supportedMimeTypes.forEach { p ->
                extensionToMimeTypeMapping[p.first] = p.second
                mimeTypeToExtensionMapping[p.second] = p.first
            }

        } catch (e: IOException) {
//            LOG.warn("IOException", e)
        }
    }

    fun findExtension(mimeType: String): String? {
        if (mimeTypeToExtensionMapping.isEmpty()) {
            log.warn("Mime type to extension mapping is empty. Please configure it.")
        }
        return mimeTypeToExtensionMapping[mimeType]
    }

    fun findMimeType(extension: String): String? {
        if (mimeTypeToExtensionMapping.isEmpty()) {
            log.warn("Extension to mime type mapping is empty. Please configure it.")
        }
        return extensionToMimeTypeMapping[extension]
    }

}