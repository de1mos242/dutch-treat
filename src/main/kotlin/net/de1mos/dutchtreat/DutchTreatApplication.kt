package net.de1mos.dutchtreat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [DispatcherServletAutoConfiguration::class, ErrorMvcAutoConfiguration::class])
class DutchTreatApplication

fun main(args: Array<String>) {
	runApplication<DutchTreatApplication>(*args)
}
