package com.project.mpesatracker.data.model

import kotlinx.datetime.LocalDateTime

data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Double,
    val timestamp: LocalDateTime,
    val reference: String,
    val balance: Double,
    val description: String,
    val senderReceiver: String
)

enum class TransactionType {
    SEND, RECEIVE, PAYBILL, BUY_GOODS, WITHDRAW, DEPOSIT,
    MSHWARI_TO, MSHWARI_FROM
} 