import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    kotlin("plugin.noarg") version "1.9.23"
    kotlin("plugin.jpa") version "1.9.23"
    kotlin("plugin.allopen") version "1.9.23"
    kotlin("plugin.lombok") version "1.9.23"
    kotlin("kapt") version "1.9.23"
}

group = "kr.goldenmine"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

kapt {
    keepJavacAnnotationProcessors = true
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("junit:junit:4.13.2")
    // gson
    implementation("com.google.code.gson:gson:2.9.1")

    // https://mvnrepository.com/artifact/com.opencsv/opencsv
    implementation("com.opencsv:opencsv:5.7.1")

    // https://mvnrepository.com/artifact/com.h2database/h2
    implementation(group="com.h2database", name="h2", version="2.1.214")

    // https://mvnrepository.com/artifact/mysql/mysql-connector-java
    implementation(group="mysql", name="mysql-connector-java", version="8.0.30")
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
