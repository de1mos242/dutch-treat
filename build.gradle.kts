import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}

plugins {
    id("org.springframework.boot") version "2.4.0"
    application
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.spring") version "1.4.21"
}

group = "net.de1mos"
version = "0.0.6-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}
val jaicf = "0.9.0"
val logback = "1.2.3"

// Main class to run application on heroku. Either JaicpPollerKt, or JaicpServerKt. Will propagate to .jar main class.
application {
    mainClassName = "net.de1mos.dutchtreat.DutchTreatApplicationKt"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven("https://repo.spring.io/milestone")
    maven("https://jitpack.io")
}

extra["testcontainersVersion"] = "1.14.3"

springBoot {
    buildInfo()
}


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.projectreactor:reactor-spring:1.0.1.RELEASE")
    implementation("io.sentry:sentry-spring-boot-starter:3.2.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.0")


    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")

    implementation("ch.qos.logback:logback-classic:$logback")

    implementation("com.justai.jaicf:core:$jaicf")
    implementation("com.justai.jaicf:jaicp:$jaicf")
    implementation("com.justai.jaicf:caila:$jaicf")
    implementation("com.justai.jaicf:telegram:$jaicf")
    implementation("com.justai.jaicf:dialogflow:$jaicf")

    implementation("org.springframework.experimental:spring-graalvm-native:0.8.3")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
        javaParameters = true
    }
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage> {
    environment("BP_BOOT_NATIVE_IMAGE", "1")
    environment(
        "BP_BOOT_NATIVE_IMAGE_BUILD_ARGUMENTS",
        "--initialize-at-run-time=io.netty.channel.kqueue.KQueueEventLoop " +
                "--enable-https " +
                "-H:DynamicProxyConfigurationResources=proxyClasses.json " +
                "--initialize-at-build-time=com.github.kotlintelegrambot.network.ApiService " +
                "-H:+AddAllCharsets"
    )

    imageName = "ddd/native"
}