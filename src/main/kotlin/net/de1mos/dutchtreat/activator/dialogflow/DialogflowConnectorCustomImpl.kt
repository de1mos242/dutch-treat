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

    /**
     *  "type": "service_account",
    "project_id": "dutchtreaten-ixex",
    "private_key_id": "956621385f6e7888a3f2e50f26cfcfbe665d134a",
    "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCorT/b1aVAoa3q\n573gE3x+4dr3ucXlSxAToh9Ja0G8+rLqEW3tJjWtOjt9NOYQznvJQX8Bcp8/46fJ\nHGv/yfLmwi85Cu1OjDrYXgscRD7GHP1oIlW5TE4mu5WY/C1VtODCdty0ci0aAoJ6\nWfFKcxBW3WawwZQt09llxmAJnwMwC7Nh3zIB3P0VhdwfbTpr3uuX+/aiO/7Diu7p\nsK9o0sg7K7hj6hBeSpRCORdQh+cusBYJ6GqiR9+0XE0SffHcD7w8KdPRPSn+RyuT\nBp2K/e227gAGUoWSUslZcvwaEvQeRnm4d4npzwB2w0obSyG6Vu9Z4vi+9z2q0Rlh\njrB2zofbAgMBAAECggEANf+0rFdifmTkDQogXp5OYj5Cv8Cgym1z2Z0yn+nu0yV8\n5nMq+lsbnve/+K7ZIjNALKdDlf6QXb8vRTJMnsgTc21h+cR2QQmigwbkTqnIYNsC\nAvbO87GqaTrvQtkkWEY+F/M+Z6QPrT51b97gYKGibvSU/ewS2HL0K2FM+HeH6Tao\na8TB3f18LLvOFxIOkMgfqcEYGA3cViicNEX9yi9DmiHqMzjd8xUWvUlHOn/XdV4k\niVY3BeHVdMas/ccVDI/IMyO2MD6Zsvl4h5cAguE5JM0iyuu895KZumAZ0qrTUQSL\n/u9/GGoc310vSHOZmrgqXPFT0J71ky8pWWccCfA2nQKBgQDc5snVGSUF27NR7pPS\n7miJW8Ke2Vkl2gXb9CmX9fz2upadA18+zugytELZ3Oqnl/J2eQyuX2p0rpD9p9mP\n1hGs54bILmVS7APh18DfE489T1O/s6g78/P9Y3Nt8bSKPrivsQhtFI8ASepIhgxD\nkgzjzFrpunvSktAXeZvpwpCb3wKBgQDDejV210pGjBGmUtd39ukqs0Ftr2WoehTU\n0RQZ2zV1b0+Ch46FDi3MsBvBGxCcYxn3vhGuaTRprdkZ4bf5ms02fkbR+I0a0JpU\nXVrQtz0mQvtERP4gp0O8TNpRrqj6oA0dZ3l72XzSvGa3VuVRbIfYpUYcYQ3J78it\nafJnnWQThQKBgQCqBm01O0xWzPqhxns7MEt/5gPPlkVasAwwdycIksqo8Yb5xV2r\nD0CDvmFyJOrsIjT6YUOUd1aOBssMogQABOnH5tDEBYnxoD2AuDr4uKpYq1UvyU1l\nD+ktkD2JUy/99Dbc6+srYlaLB1lvhtWrsJ+BOoqDOgkSU/QdDXonG52IiwKBgEgH\nfy3kVN07/cDw466zz+VJGkHlkKsNGSH2TNbePIuiUYUCmFSVDfFPr1pvs6Y8qjPs\n2K0Q3RxX0BjGYLlb/mQnEceLM8t+tG+D2tPvwVuY8OeOGKpFmCMJmqTgnCmF0m9U\nUqJ9WI56l8rU/TkMqd39wKDybldLwQGNeaIOj/LVAoGBAJgg0/JqhD1XwZYSBO0R\nbJj9+8iFvM/pbp256DCiBI5hHeQySdKx4XEuKXFS3NQL31TUsd+WipPHzPe5672T\nPyYbJH6l8YTPBK4TQlsOnTIU8mCXV3JNBW0q5AjI6WvYReWasLWPanrBiquWemE1\n41TBAXbx4Q/1rsFh8NU2gi18\n-----END PRIVATE KEY-----\n",
    "client_email": "jaicf-dialogflow@dutchtreaten-ixex.iam.gserviceaccount.com",
    "client_id": "108252415850613878773",
    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
    "token_uri": "https://oauth2.googleapis.com/token",
    "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
    "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/jaicf-dialogflow%40dutchtreaten-ixex.iam.gs
     */
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