package net.de1mos.dutchtreat.jaicf

import com.justai.jaicf.activator.regex.regex
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.context.ActionContext
import io.sentry.Sentry
import net.de1mos.dutchtreat.repositories.ChannelType
import net.de1mos.dutchtreat.repositories.Event
import net.de1mos.dutchtreat.services.UserPreferencesService
import java.math.BigDecimal
import java.text.DecimalFormat

open class BaseStatesCollection(private val userPreferencesService: UserPreferencesService) {

    protected fun wrapAction(actionContext: ActionContext, block: () -> Unit) {
        try {
            processUser(actionContext)
            block()
        } catch (e: Exception) {
            Sentry.captureException(e, "Catch in scenario")
        }
    }

    private fun processUser(actionContext: ActionContext) {
        val telegramUser = actionContext.request.telegram?.message?.from
        userPreferencesService.createOrUpdateUserInfo(
            actionContext.context.clientId,
            lang = telegramUser?.languageCode ?: "en",
            firstName = telegramUser?.firstName,
            lastName = telegramUser?.lastName,
            username = telegramUser?.username,
            channelType = if (telegramUser != null) ChannelType.TELEGRAM else ChannelType.UNKNOWN
        )
    }

    protected fun ActionContext.getValFromRegex(group: String = "val") = activator.regex?.group(group)!!.trim()
    protected fun ActionContext.getSafeValFromRegex(group: String = "val") =
        activator.regex?.matcher?.group(group)?.trim()

    protected fun ActionContext.getUserEvent(): Event? {
        val e = userPreferencesService.getUserCurrentEvent(context.clientId)
        if (e == null) {
            reactions.say("There is no current event, but you can create a new one")
            return null
        }
        return e
    }

    protected fun ActionContext.toBigDecimal(str: String): BigDecimal? {
        return try {
            BigDecimal(str)
        } catch (e: NumberFormatException) {
            reactions.say("Can't recognize $str as amount of money")
            null
        }
    }

    protected fun BigDecimal.toPrettyString(): String {
        return DecimalFormat("0.00").format(this).replace(".", ",")
    }
}