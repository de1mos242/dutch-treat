package net.de1mos.dutchtreat.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class DialogflowConfig(@Value("\${dialogflow}") val credentials: String)