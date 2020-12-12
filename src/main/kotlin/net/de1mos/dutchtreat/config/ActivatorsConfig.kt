package net.de1mos.dutchtreat.config

import com.justai.jaicf.activator.ActivatorFactory
import com.justai.jaicf.activator.dialogflow.DialogflowAgentConfig
import com.justai.jaicf.activator.dialogflow.DialogflowConnector
import com.justai.jaicf.activator.dialogflow.DialogflowIntentActivator
import com.justai.jaicf.activator.regex.RegexActivator

class ActivatorsConfig(private val dialogflowConfig: DialogflowConfig) {

    fun getActivators(): Array<ActivatorFactory> {
        val dialogFlowActivatorEn = DialogflowIntentActivator.Factory(
            DialogflowConnector(
                DialogflowAgentConfig(
                language = "en",
                credentials = dialogflowConfig.credentialsInputStream
            )
            )
        )
        val dialogFlowActivatorRu = DialogflowIntentActivator.Factory(
            DialogflowConnector(
                DialogflowAgentConfig(
                language = "ru",
                credentials = dialogflowConfig.credentialsInputStream
            )
            )
        )
        return arrayOf(
            RegexActivator,
            dialogFlowActivatorEn,
            dialogFlowActivatorRu
        )
    }
}