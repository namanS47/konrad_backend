package com.example.konrad.utility

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

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
        resource.file.forEachLine { l ->
            val tokens = l.split(",")
            supportedMimeTypes.add(Pair(tokens.first(), tokens.last()))
        }
        supportedMimeTypes.forEach { p ->
            extensionToMimeTypeMapping[p.first] = p.second
            mimeTypeToExtensionMapping[p.second] = p.first
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