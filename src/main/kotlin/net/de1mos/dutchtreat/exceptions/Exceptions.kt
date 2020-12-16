package net.de1mos.dutchtreat.exceptions

class ParticipantNotFoundException(val name: String) : RuntimeException()
class PurchaseNotFoundException(val position: Int) : RuntimeException()
class TransferNotFoundException(val position: Int) : RuntimeException()
class NoPurchasesException : RuntimeException()
class EventNotFoundException(val eventName: String) : RuntimeException()
class InvitationCodeNotFoundException : RuntimeException()

class SetWebhookFailedException(val responseCode: Int, val responseText: String?) : RuntimeException()

class UserPreferencesNotFound(val userId: String) : RuntimeException()
