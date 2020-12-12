package net.de1mos.dutchtreat.activator.dialogflow

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.ServiceAccountCredentialsCustomImpl
import com.google.cloud.dialogflow.v2.ContextsClient
import com.google.cloud.dialogflow.v2.ContextsSettings
import com.google.cloud.dialogflow.v2.DetectIntentRequest
import com.google.cloud.dialogflow.v2.EventInput
import com.google.cloud.dialogflow.v2.QueryInput
import com.google.cloud.dialogflow.v2.QueryParameters
import com.google.cloud.dialogflow.v2.QueryResult
import com.google.cloud.dialogflow.v2.SessionName
import com.google.cloud.dialogflow.v2.SessionsClient
import com.google.cloud.dialogflow.v2.SessionsSettings
import com.google.cloud.dialogflow.v2.TextInput
import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.api.BotRequestType
import java.net.URI

data class DialogflowAgentConfigCustomImpl(
    val language: String,
    val credentials: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
class CredentialsDto(
    @JsonProperty("client_id") var clientId: String = "",
    @JsonProperty("client_email") var clientEmail: String = "",
    @JsonProperty("private_key") var privateKey: String = "",
    @JsonProperty("private_key_id") var privateKeyId: String = "",
    @JsonProperty("token_uri") var tokenUri: String = "",
    @JsonProperty("project_id") var projectId: String = ""
)

class DialogflowConnectorCustomImpl(private val config: DialogflowAgentConfigCustomImpl) {

    private val sessionSettings: SessionsSettings
    private val contextsSettings: ContextsSettings
    private val projectId: String

    init {
        val dto = ObjectMapper().readTree(config.credentials)

        val credentials = ServiceAccountCredentialsCustomImpl(
            dto.get("client_id").asText(),
            dto.get("client_email").asText(),
            dto.get("private_key").asText(),
            dto.get("private_key_id").asText(),
            URI(dto.get("token_uri").asText()),
            dto.get("project_id").asText()
        )
        projectId = credentials.projectId
        sessionSettings = SessionsSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build()
        contextsSettings = ContextsSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build()
    }

    private fun createQuery(request: BotRequest): QueryInput.Builder? {
        val query = QueryInput.newBuilder()

        return when (request.type) {
            BotRequestType.EVENT -> query.setEvent(
                EventInput.newBuilder()
                    .setName(request.input)
                    .setLanguageCode(config.language).build()
            )

            BotRequestType.QUERY -> query.setText(
                TextInput.newBuilder()
                    .setText(request.input)
                    .setLanguageCode(config.language)
            )

            else -> null
        }
    }

    private fun detectIntent(query: QueryInput, session: SessionName, params: QueryParameters): QueryResult {
        val client = SessionsClient.create(sessionSettings)
        try {
            return client.detectIntent(
                DetectIntentRequest.newBuilder()
                    .setQueryInput(query)
                    .setSession(session.toString())
                    .setQueryParams(params)
                    .build()
            ).queryResult
        } finally {
            client.close()
        }
    }

    fun detectIntent(request: BotRequest, params: QueryParameters) = createQuery(request)?.let {
        detectIntent(it.build(), request.sessionName, params)
    }

    fun deleteAllContexts(request: BotRequest) {
        val client = ContextsClient.create(contextsSettings)
        try {
            client.deleteAllContexts(request.sessionName)
        } finally {
            client.close()
        }
    }

    private val BotRequest.sessionName
        get() = SessionName.of(projectId, clientId)
}