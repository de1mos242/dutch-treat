import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.5.RELEASE"
	application
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	kotlin("jvm") version "1.3.72"
	kotlin("plugin.spring") version "1.3.72"
	id("com.justai.jaicf.jaicp-build-plugin") version "0.1.1"
}

group = "net.de1mos"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}
val jaicf = "0.8.2"
val logback = "1.2.3"

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
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
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


tasks.create("stage") {
	dependsOn("shadowJar")
}

tasks.withType<com.justai.jaicf.plugins.jaicp.build.JaicpBuild> {
	mainClassName.set(application.mainClassName)
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
	isZip64 = true
	// Required for Spring
	mergeServiceFiles()
	append("META-INF/spring.handlers")
	append("META-INF/spring.schemas")
	append("META-INF/spring.tooling")
	transform(PropertiesFileTransformer().apply {
		paths = listOf("META-INF/spring.factories")
		mergeStrategy = "append"
	})
}