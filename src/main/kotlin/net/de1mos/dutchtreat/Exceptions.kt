package net.de1mos.dutchtreat

class ParticipantNotFoundException(val name: String) : RuntimeException()
class PurchaseNotFoundException(val position: Int) : RuntimeException()
class NoPurchasesException : RuntimeException()