package com.todeal.infrastructure.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class S3UploadService(
    private val amazonS3: AmazonS3,
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String
) {
    fun upload(file: MultipartFile): String {
        val fileName = UUID.randomUUID().toString() + "_" + file.originalFilename
        val metadata = ObjectMetadata().apply {
            contentLength = file.size
            contentType = file.contentType
        }

        amazonS3.putObject(bucket, fileName, file.inputStream, metadata)
        return amazonS3.getUrl(bucket, fileName).toString()
    }
}
