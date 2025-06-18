package com.todeal.infrastructure.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import net.coobird.thumbnailator.Thumbnails
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

@Service
class S3UploadService(
    private val amazonS3: AmazonS3,
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String
) {
    companion object {
        private const val MAX_FILE_SIZE_MB = 5
    }

    fun upload(file: MultipartFile): String {
        if (file.size > MAX_FILE_SIZE_MB * 1024 * 1024) {
            throw IllegalArgumentException("파일은 최대 ${MAX_FILE_SIZE_MB}MB까지 업로드 가능합니다.")
        }

        val fileName = UUID.randomUUID().toString() + ".jpg"

        val outputStream = ByteArrayOutputStream()
        Thumbnails.of(file.inputStream)
            .size(1024, 1024)
            .outputFormat("jpg")
            .outputQuality(0.85f)
            .toOutputStream(outputStream)

        val bytes = outputStream.toByteArray()
        val metadata = ObjectMetadata().apply {
            contentLength = bytes.size.toLong()
            contentType = "image/jpeg"
        }

        amazonS3.putObject(bucket, fileName, ByteArrayInputStream(bytes), metadata)
        return amazonS3.getUrl(bucket, fileName).toString()
    }

    fun delete(imageUrl: String) {
        val fileName = imageUrl.substringAfterLast("/")  // S3 경로에서 파일명 추출
        amazonS3.deleteObject(bucket, fileName)
    }
}
