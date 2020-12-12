import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
	repositories {
		jcenter()
	}
	dependencies {
		classpath("org.koin:koin-gradle-plugin:2.2.1")
		classpath("com.github.jengelman.gradle.plugins:shadow:2.0.1")
	}
}
apply(plugin = "koin")
apply(plugin = "com.github.johnrengelman.shadow")

plugins {
	application
	kotlin("jvm") version "1.4.0"
	id("com.github.johnrengelman.shadow") version "6.1.0"
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

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("io.ktor:ktor-server-core:$ktorVersion")
	implementation("io.ktor:ktor-server-netty:$ktorVersion")
	implementation("io.ktor:ktor-client-core:$ktorVersion")
	implementation("org.koin:koin-core:$koinVersion")
	implementation("io.ktor:ktor-client-cio:$ktorVersion")
	testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
	testImplementation("org.koin:koin-test:$koinVersion")

	implementation("org.litote.kmongo:kmongo:4.2.3")

	implementation("io.sentry:sentry:3.2.0")

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

kotlin {
	experimental {
		coroutines=org.jetbrains.kotlin.gradle.dsl.Coroutines.ENABLE
	}
}



tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("dutch-treat")
		archiveVersion.set(null as String?)
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to application.mainClassName))
        }
    }
}

tasks {
	build {
		dependsOn(shadowJar)
	}
}