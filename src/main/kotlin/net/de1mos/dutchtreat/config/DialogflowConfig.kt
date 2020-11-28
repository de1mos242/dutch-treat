package net.de1mos.dutchtreat.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.io.InputStream

@Configuration
class DialogflowConfig(@Value("\${dialogflow}") private val dialogflowCredentials: String) {
    final val credentialsInputStream: InputStream
        get() = dialogflowCredentials.byteInputStream()
}