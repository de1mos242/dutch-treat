package net.de1mos.dutchtreat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class DutchTreatApplication

object Dev {
	@JvmStatic
	fun main(args: Array<String>) {
		System.setProperty("spring.profiles.active", "local")
		net.de1mos.dutchtreat.main(args)
	}
}

fun main(args: Array<String>) {
	runApplication<DutchTreatApplication>(*args)
}
