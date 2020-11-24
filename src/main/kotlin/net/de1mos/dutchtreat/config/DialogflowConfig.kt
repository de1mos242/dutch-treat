package net.de1mos.dutchtreat.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.io.InputStream
import java.util.*

@Configuration
class DialogflowConfig(@Value("\${dialogflow}") private val encodedCredentials: String) {
    final val credentialsInputStream: InputStream
        get() = String(Base64.getDecoder().decode(encodedCredentials)).byteInputStream()
}