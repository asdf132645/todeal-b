package com.todeal.global.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class S3Config(
    private val env: Environment
) {
    @Bean
    fun amazonS3(): AmazonS3 {
        val accessKey = env.getRequiredProperty("cloud.aws.credentials.access-key")
        val secretKey = env.getRequiredProperty("cloud.aws.credentials.secret-key")
        val region = env.getProperty("cloud.aws.region.static", "ap-northeast-2")

        val credentials = BasicAWSCredentials(accessKey, secretKey)

        return AmazonS3ClientBuilder.standard()
            .withRegion(region)
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .build()
    }
}
