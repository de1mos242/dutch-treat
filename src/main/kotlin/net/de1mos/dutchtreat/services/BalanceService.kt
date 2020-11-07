package net.de1mos.dutchtreat.services

import net.de1mos.dutchtreat.repositories.Event
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class BalanceService {

    fun calculateBalance(event: Event): List<BalanceRow> {
        if (event.participants == null || event.participants.isEmpty()) {
            return emptyList()
        }
        val participantsCount = event.participants.size.toBigDecimal()
        return event.participants.map {
            val purchases = event.purchases?.filter { p -> p.buyerId == it.id }?.map { p -> p.amount }?.plus(BigDecimal.ZERO)?.reduce(BigDecimal::add)
                    ?: BigDecimal.ZERO

            val consumes = event.purchases?.filter { p -> p.consumerIds == null || p.consumerIds.contains(it.id) }?.map { p ->
                p.amount.divide(if (p.consumerIds == null) participantsCount else p.consumerIds.size.toBigDecimal(), 4, RoundingMode.HALF_DOWN)
            }?.plus(BigDecimal.ZERO)?.reduce(BigDecimal::add) ?: BigDecimal.ZERO

            val sends = event.transfers?.filter { t -> t.senderId == it.id }?.map { t -> t.amount }?.plus(BigDecimal.ZERO)?.reduce(BigDecimal::add)
                    ?: BigDecimal.ZERO

            val receives = event.transfers?.filter { t -> t.receiverId == it.id }?.map { t -> t.amount }?.plus(BigDecimal.ZERO)?.reduce(BigDecimal::add)
                    ?: BigDecimal.ZERO

            val balance = purchases.minus(consumes).plus(sends).minus(receives)
            BalanceRow(it.name, if (balance.abs().compareTo(BigDecimal(0.5)) < 1) BigDecimal.ZERO else balance)
        }
    }

    data class BalanceRow(val participantName: String, val balance: BigDecimal)
}