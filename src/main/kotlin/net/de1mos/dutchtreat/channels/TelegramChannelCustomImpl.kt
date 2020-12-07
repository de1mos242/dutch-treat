package net.de1mos.dutchtreat.channels


import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandler
import com.github.kotlintelegrambot.dispatcher.handlers.ContactHandler
import com.github.kotlintelegrambot.dispatcher.handlers.LocationHandler
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.AnimationHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.AudioHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.DocumentHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.GameHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.PhotosHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.StickerHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.VideoHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.VideoNoteHandler
import com.github.kotlintelegrambot.dispatcher.handlers.media.VoiceHandler
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.updater.Updater
import com.google.gson.Gson
import com.justai.jaicf.api.BotApi
import com.justai.jaicf.channel.http.HttpBotChannel
import com.justai.jaicf.channel.http.HttpBotRequest
import com.justai.jaicf.channel.http.HttpBotResponse
import com.justai.jaicf.channel.http.asJsonHttpBotResponse
import com.justai.jaicf.channel.telegram.TelegramAnimationRequest
import com.justai.jaicf.channel.telegram.TelegramAudioRequest
import com.justai.jaicf.channel.telegram.TelegramBotRequest
import com.justai.jaicf.channel.telegram.TelegramContactRequest
import com.justai.jaicf.channel.telegram.TelegramDocumentRequest
import com.justai.jaicf.channel.telegram.TelegramGameRequest
import com.justai.jaicf.channel.telegram.TelegramLocationRequest
import com.justai.jaicf.channel.telegram.TelegramPhotosRequest
import com.justai.jaicf.channel.telegram.TelegramQueryRequest
import com.justai.jaicf.channel.telegram.TelegramReactions
import com.justai.jaicf.channel.telegram.TelegramStickerRequest
import com.justai.jaicf.channel.telegram.TelegramTextRequest
import com.justai.jaicf.channel.telegram.TelegramVideoNoteRequest
import com.justai.jaicf.channel.telegram.TelegramVideoRequest
import com.justai.jaicf.channel.telegram.TelegramVoiceRequest
import com.justai.jaicf.context.RequestContext
import com.justai.jaicf.helpers.kotlin.PropertyWithBackingField

class TelegramChannelCustomImpl(
    override val botApi: BotApi,
    private val telegramBotToken: String,
    private val telegramApiUrl: String = "https://api.telegram.org/"
) : HttpBotChannel {

    private val gson = Gson()

    private lateinit var botUpdater: Updater


    private val bot = bot {
        apiUrl = telegramApiUrl
        token = telegramBotToken
        botUpdater = updater
    }

    fun process(request: TelegramBotRequest, update: Update) {
        botApi.process(request, TelegramReactions(bot, request), RequestContext.fromHttp(update.httpBotRequest))
    }

    val dispatchHandlers = listOf(
        TextHandler { _, update ->
            update.message?.let {
                process(TelegramTextRequest(it), update)
            }
        },

        CallbackQueryHandler { _, update ->
            update.callbackQuery?.let {
                process(TelegramQueryRequest(it.message!!, it.data), update)
            }
        },

        LocationHandler { _, update, location ->
            update.message?.let {
                process(TelegramLocationRequest(it, location), update)
            }
        },

        ContactHandler { _, update, contact ->
            update.message?.let {
                process(TelegramContactRequest(it, contact), update)
            }
        },

        AudioHandler { _, update, audio ->
            update.message?.let {
                process(TelegramAudioRequest(it, audio), update)
            }
        },

        DocumentHandler { _, update, document ->
            update.message?.let {
                process(TelegramDocumentRequest(it, document), update)
            }
        },

        AnimationHandler { _, update, animation ->
            update.message?.let {
                process(TelegramAnimationRequest(it, animation), update)
            }
        },

        GameHandler { _, update, game ->
            update.message?.let {
                process(TelegramGameRequest(it, game), update)
            }
        },

        PhotosHandler { _, update, list ->
            update.message?.let {
                process(TelegramPhotosRequest(it, list), update)
            }
        },

        StickerHandler { _, update, sticker ->
            update.message?.let {
                process(TelegramStickerRequest(it, sticker), update)
            }
        },

        VideoHandler { _, update, video ->
            update.message?.let {
                process(TelegramVideoRequest(it, video), update)
            }
        },

        VideoNoteHandler { _, update, videoNote ->
            update.message?.let {
                process(TelegramVideoNoteRequest(it, videoNote), update)
            }
        },

        VoiceHandler { _, update, voice ->
            update.message?.let {
                process(TelegramVoiceRequest(it, voice), update)
            }
        }
    )

    override fun process(request: HttpBotRequest): HttpBotResponse {
        val update = gson.fromJson(request.receiveText(), Update::class.java)
        update.httpBotRequest = request

        return process(update).asJsonHttpBotResponse()
    }

    fun process(update: Update): String {
        dispatchHandlers.filter { it.checkUpdate(update) }
            .forEach { it.handlerCallback(bot, update) }
        return "Ok"
    }

}

internal var Update.httpBotRequest: HttpBotRequest by PropertyWithBackingField {
    HttpBotRequest("".byteInputStream())
}