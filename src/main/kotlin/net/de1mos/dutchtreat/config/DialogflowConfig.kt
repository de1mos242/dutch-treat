package net.de1mos.dutchtreat.config

import java.io.InputStream
import java.util.*

class DialogflowConfig(private val dialogflowCredentials: String) {
    val credentialsInputStream: InputStream
        get() = String(Base64.getDecoder().decode(dialogflowCredentials)).byteInputStream()
}