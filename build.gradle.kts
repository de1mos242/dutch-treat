import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	application
	kotlin("jvm") version "1.4.0"
}

group = "net.de1mos"
version = "0.0.7-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

val jaicf = "0.9.0"
val logback = "1.2.3"
val ktorVersion = "1.4.0"
val koinVersion = "2.2.1"

// Main class to run application on heroku. Either JaicpPollerKt, or JaicpServerKt. Will propagate to .jar main class.
application {
	mainClassName = "net.de1mos.dutchtreat.DutchTreatApplicationKt"
}

repositories {
	mavenLocal()
	mavenCentral()
	jcenter()
	maven("https://jitpack.io")
}

extra["testcontainersVersion"] = "1.14.3"

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("io.ktor:ktor-server-core:$ktorVersion")
	implementation("io.ktor:ktor-server-netty:$ktorVersion")

	implementation("org.koin:koin-core:$koinVersion")
	testImplementation("org.koin:koin-test:$koinVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")

    implementation("ch.qos.logback:logback-classic:$logback")

    implementation("com.justai.jaicf:core:$jaicf")
    implementation("com.justai.jaicf:jaicp:$jaicf")
    implementation("com.justai.jaicf:caila:$jaicf")
    implementation("com.justai.jaicf:telegram:$jaicf")
    implementation("com.justai.jaicf:dialogflow:$jaicf")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}