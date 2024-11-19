package com.project.mpesatracker.util

import com.project.mpesatracker.data.model.Transaction
import com.project.mpesatracker.data.model.TransactionType
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import java.util.regex.Pattern

class MpesaSmsParser {
    
    fun parseMessage(message: String): Transaction? {
        return when {
            message.contains("received", ignoreCase = true) -> parseReceivedTransaction(message)
            message.contains("sent to", ignoreCase = true) -> parseSentTransaction(message)
            message.contains("paid to", ignoreCase = true) -> parsePaybillTransaction(message)
            message.contains("Buy Goods", ignoreCase = true) -> parseBuyGoodsTransaction(message)
            message.contains("Withdraw", ignoreCase = true) -> parseWithdrawTransaction(message)
            message.contains("Deposit", ignoreCase = true) -> parseDepositTransaction(message)
            message.contains("transferred to M-Shwari", ignoreCase = true) -> parseMshwariToTransaction(message)
            message.contains("transferred from M-Shwari", ignoreCase = true) -> parseMshwariFromTransaction(message)
            else -> null
        }
    }

    private fun parseAmount(amountStr: String?): Double {
        return amountStr?.replace(",", "")?.toDoubleOrNull() ?: 0.0
    }

    private fun parseMshwariToTransaction(message: String): Transaction? {
        val pattern = Pattern.compile(
            "Ksh(\\d+(?:,\\d+)?(?:\\.\\d+)?).* transferred to M-Shwari account on (\\d{1,2}/\\d{1,2}/\\d{2}) at (\\d{1,2}:\\d{2} [AP]M).*M-PESA balance is Ksh(\\d+(?:,\\d+)?(?:\\.\\d+)?)",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = pattern.matcher(message)
        
        return if (matcher.find()) {
            val timestamp = parseDateTime(matcher.group(2), matcher.group(3))
            Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.MSHWARI_TO,
                amount = -parseAmount(matcher.group(1)),
                timestamp = timestamp,
                reference = extractReference(message),
                balance = parseAmount(matcher.group(4)),
                description = message,
                senderReceiver = "M-Shwari"
            )
        } else null
    }

    private fun parseMshwariFromTransaction(message: String): Transaction? {
        val pattern = Pattern.compile(
            "Ksh(\\d+(?:,\\d+)?(?:\\.\\d+)?).* transferred from M-Shwari account on (\\d{1,2}/\\d{1,2}/\\d{2}) at (\\d{1,2}:\\d{2} [AP]M).*M-PESA balance is Ksh(\\d+(?:,\\d+)?(?:\\.\\d+)?)",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = pattern.matcher(message)
        
        return if (matcher.find()) {
            val timestamp = parseDateTime(matcher.group(2), matcher.group(3))
            Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.MSHWARI_FROM,
                amount = parseAmount(matcher.group(1)),
                timestamp = timestamp,
                reference = extractReference(message),
                balance = parseAmount(matcher.group(4)),
                description = message,
                senderReceiver = "M-Shwari"
            )
        } else null
    }

    private fun parseReceivedTransaction(message: String): Transaction? {
        val pattern = Pattern.compile(
            "(?:Confirmed\\.|Confirmed)\\.?\\s*(?:You have )?received Ksh(\\d+(?:,\\d+)?(?:\\.\\d+)?).* from (.*?) (?:\\d+)? on (\\d{1,2}/\\d{1,2}/\\d{2}) at (\\d{1,2}:\\d{2} [AP]M).*New M-PESA balance is Ksh(\\d+(?:,\\d+)?(?:\\.\\d+)?)",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = pattern.matcher(message)
        
        return if (matcher.find()) {
            val timestamp = parseDateTime(matcher.group(3), matcher.group(4))
            Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.RECEIVE,
                amount = parseAmount(matcher.group(1)),
                timestamp = timestamp,
                reference = extractReference(message),
                balance = parseAmount(matcher.group(5)),
                description = message,
                senderReceiver = matcher.group(2)?.trim() ?: ""
            )
        } else null
    }

    private fun parseSentTransaction(message: String): Transaction? {
        val pattern = Pattern.compile(
            "(?:Confirmed\\.|Confirmed)\\.?\\s*Ksh(\\d+(?:,\\d+)?(?:\\.\\d+)?).* sent to (.*?) (?:for account .*? )?on (\\d{1,2}/\\d{1,2}/\\d{2}) at (\\d{1,2}:\\d{2} [AP]M).*New M-PESA balance is Ksh(\\d+(?:,\\d+)?(?:\\.\\d+)?)",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = pattern.matcher(message)
        
        return if (matcher.find()) {
            val timestamp = parseDateTime(matcher.group(3), matcher.group(4))
            Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.SEND,
                amount = -parseAmount(matcher.group(1)),
                timestamp = timestamp,
                reference = extractReference(message),
                balance = parseAmount(matcher.group(5)),
                description = message,
                senderReceiver = matcher.group(2)?.trim() ?: ""
            )
        } else null
    }

    private fun parseDateTime(date: String?, time: String?): LocalDateTime {
        if (date == null || time == null) return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        
        val (day, month, year) = date.split("/").map { it.toInt() }
        val (hourMin, period) = time.split(" ")
        val (hour, minute) = hourMin.split(":").map { it.toInt() }
        
        var adjustedHour = hour
        if (period.equals("PM", ignoreCase = true) && hour != 12) {
            adjustedHour += 12
        } else if (period.equals("AM", ignoreCase = true) && hour == 12) {
            adjustedHour = 0
        }
        
        return LocalDateTime(
            year = 2000 + year,
            monthNumber = month,
            dayOfMonth = day,
            hour = adjustedHour,
            minute = minute
        )
    }

    private fun parsePaybillTransaction(message: String): Transaction? {
        val pattern = Pattern.compile(
            "Ksh(\\d+(?:,\\d+)?(?:\\.\\d+)?).* paid to ([^.]*?)(?:\\.|(?= on)).*(?:on|On) (\\d{1,2}/\\d{1,2}/\\d{2}) at (\\d{1,2}:\\d{2} [AP]M).*(?:New M-PESA balance is|M-PESA balance is) Ksh(\\d+(?:,\\d+)?(?:\\.\\d+)?)",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = pattern.matcher(message)
        
        return if (matcher.find()) {
            val timestamp = parseDateTime(matcher.group(3), matcher.group(4))
            Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.PAYBILL,
                amount = -parseAmount(matcher.group(1)),
                timestamp = timestamp,
                reference = extractReference(message),
                balance = parseAmount(matcher.group(5)),
                description = message,
                senderReceiver = matcher.group(2)?.trim() ?: ""
            )
        } else null
    }

    private fun parseBuyGoodsTransaction(message: String): Transaction? {
        val pattern = Pattern.compile(
            "Ksh(\\d+(?:\\.\\d+)?).* paid to (.*?) for Buy Goods.*\\..*New M-PESA balance is Ksh(\\d+(?:\\.\\d+)?)"
        )
        val matcher = pattern.matcher(message)
        
        return if (matcher.find()) {
            Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.BUY_GOODS,
                amount = -(matcher.group(1)?.toDoubleOrNull() ?: 0.0),
                timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                reference = extractReference(message),
                balance = matcher.group(3)?.toDoubleOrNull() ?: 0.0,
                description = message,
                senderReceiver = matcher.group(2) ?: ""
            )
        } else null
    }

    private fun parseWithdrawTransaction(message: String): Transaction? {
        val pattern = Pattern.compile(
            "Ksh(\\d+(?:\\.\\d+)?).* withdrawn from (.*?) on.*\\..*New M-PESA balance is Ksh(\\d+(?:\\.\\d+)?)"
        )
        val matcher = pattern.matcher(message)
        
        return if (matcher.find()) {
            Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.WITHDRAW,
                amount = -(matcher.group(1)?.toDoubleOrNull() ?: 0.0),
                timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                reference = extractReference(message),
                balance = matcher.group(3)?.toDoubleOrNull() ?: 0.0,
                description = message,
                senderReceiver = matcher.group(2) ?: "ATM Withdrawal"
            )
        } else null
    }

    private fun parseDepositTransaction(message: String): Transaction? {
        val pattern = Pattern.compile(
            "Ksh(\\d+(?:\\.\\d+)?).* deposited to.*on.*\\..*New M-PESA balance is Ksh(\\d+(?:\\.\\d+)?)"
        )
        val matcher = pattern.matcher(message)
        
        return if (matcher.find()) {
            Transaction(
                id = UUID.randomUUID().toString(),
                type = TransactionType.DEPOSIT,
                amount = matcher.group(1)?.toDoubleOrNull() ?: 0.0,
                timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                reference = extractReference(message),
                balance = matcher.group(2)?.toDoubleOrNull() ?: 0.0,
                description = message,
                senderReceiver = "Cash Deposit"
            )
        } else null
    }

    private fun extractReference(message: String): String {
        // Try to find reference number in the message
        val refPattern = Pattern.compile("ref\\. no\\.?\\s*(\\w+)", Pattern.CASE_INSENSITIVE)
        val matcher = refPattern.matcher(message)
        return if (matcher.find()) {
            matcher.group(1) ?: ""
        } else ""
    }
} 