plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("gg.jte.gradle") version "3.1.16"
    kotlin("plugin.jpa") version "1.9.25"
}

group = "com.todeal"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.cdimascio:java-dotenv:5.2.2")
    implementation("software.amazon.awssdk:s3:2.20.89") // 최신 AWS SDK v2
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux") // WebClient 사용 시
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.664")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("com.sun.mail:jakarta.mail:2.0.1")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.json:json:20231013") // ✅ 최신 안정 버전
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1") // ✅ dotenv 지원 라이브러리
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5") // or :jjwt-gson
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.22.0")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.google.firebase:firebase-admin:9.2.0") // 버전은 필요 시 최신으로
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("gg.jte:jte-spring-boot-starter-3:3.1.16")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

jte {
    generate()
    binaryStaticContent = true
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
tasks.withType<JavaExec>().configureEach {
    standardOutput = System.out
    errorOutput = System.err
    isIgnoreExitValue = true
}
