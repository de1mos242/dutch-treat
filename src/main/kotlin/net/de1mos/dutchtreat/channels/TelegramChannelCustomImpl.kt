package net.de1mos.dutchtreat.channels


import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.animation
import com.github.kotlintelegrambot.dispatcher.audio
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.contact
import com.github.kotlintelegrambot.dispatcher.document
import com.github.kotlintelegrambot.dispatcher.game
import com.github.kotlintelegrambot.dispatcher.location
import com.github.kotlintelegrambot.dispatcher.photos
import com.github.kotlintelegrambot.dispatcher.sticker
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.dispatcher.video
import com.github.kotlintelegrambot.dispatcher.videoNote
import com.github.kotlintelegrambot.dispatcher.voice
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

        dispatch {
            fun process(request: TelegramBotRequest, update: Update) {
                botApi.process(request, TelegramReactions(bot, request), RequestContext.fromHttp(update.httpBotRequest))
            }

            text { _, update ->
                update.message?.let {
                    process(TelegramTextRequest(it), update)
                }
            }

            callbackQuery { _, update ->
                update.callbackQuery?.let {
                    process(TelegramQueryRequest(it.message!!, it.data), update)
                }
            }

            location { _, update, location ->
                update.message?.let {
                    process(TelegramLocationRequest(it, location), update)
                }
            }

            contact { _, update, contact ->
                update.message?.let {
                    process(TelegramContactRequest(it, contact), update)
                }
            }

            audio { _, update, audio ->
                update.message?.let {
                    process(TelegramAudioRequest(it, audio), update)
                }
            }

            document { _, update, document ->
                update.message?.let {
                    process(TelegramDocumentRequest(it, document), update)
                }
            }

            animation { _, update, animation ->
                update.message?.let {
                    process(TelegramAnimationRequest(it, animation), update)
                }
            }

            game { _, update, game ->
                update.message?.let {
                    process(TelegramGameRequest(it, game), update)
                }
            }

            photos { _, update, list ->
                update.message?.let {
                    process(TelegramPhotosRequest(it, list), update)
                }
            }

            sticker { _, update, sticker ->
                update.message?.let {
                    process(TelegramStickerRequest(it, sticker), update)
                }
            }

            video { _, update, video ->
                update.message?.let {
                    process(TelegramVideoRequest(it, video), update)
                }
            }

            videoNote { _, update, videoNote ->
                update.message?.let {
                    process(TelegramVideoNoteRequest(it, videoNote), update)
                }
            }

            voice { _, update, voice ->
                update.message?.let {
                    process(TelegramVoiceRequest(it, voice), update)
                }
            }
        }
    }

    override fun process(request: HttpBotRequest): HttpBotResponse? {
        val update = gson.fromJson(request.receiveText(), Update::class.java)
        update.httpBotRequest = request
        bot.processUpdate(update)
        return "Ok".asJsonHttpBotResponse()
    }

    fun startCheckingUpdates() {
        botUpdater.startCheckingUpdates()
    }
}

internal var Update.httpBotRequest: HttpBotRequest by PropertyWithBackingField {
    HttpBotRequest("".byteInputStream())
}