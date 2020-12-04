package net.de1mos.dutchtreat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [DispatcherServletAutoConfiguration::class, ErrorMvcAutoConfiguration::class])
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
