import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
}

group = "cz.phishingalert"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.3")
    implementation("com.google.guava:guava:33.1.0-jre")
    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.49.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.49.0")
    implementation("com.h2database:h2:2.2.224")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.amqp:spring-rabbit-test")
    testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}