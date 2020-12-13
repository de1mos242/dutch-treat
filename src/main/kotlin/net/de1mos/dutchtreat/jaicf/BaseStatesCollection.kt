package net.de1mos.dutchtreat.jaicf

import com.justai.jaicf.activator.regex.regex
import com.justai.jaicf.context.ActionContext
import io.sentry.Sentry
import net.de1mos.dutchtreat.repositories.Event
import net.de1mos.dutchtreat.services.UserPreferencesService
import java.math.BigDecimal
import java.text.DecimalFormat

open class BaseStatesCollection(private val userPreferencesService: UserPreferencesService) {

    protected fun wrapSentry(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            Sentry.captureException(e, "Catch in scenario")
        }
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