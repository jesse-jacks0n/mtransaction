package com.project.mpesatracker.util//package com.project.mpesatracker.util
//
//import com.project.mpesatracker.data.model.Transaction
//import com.project.mpesatracker.data.model.TransactionType
//import java.text.SimpleDateFormat
//import java.util.*
//
//object SmsParser {
//    private val mpesaPattern = Regex("""(?i)MPESA""")
//    private val amountPattern = Regex("""Ksh[.,\s]*(\d+[.,]\d+)""")
//    private val datePattern = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
//
//    fun parseMpesaSms(message: String, date: Date): Transaction? {
//        if (!message.contains(mpesaPattern)) return null
//
//        return try {
//            val type = determineTransactionType(message)
//            val amount = extractAmount(message)
//            val reference = extractReference(message)
//            val balance = extractBalance(message)
//            val senderReceiver = extractSenderReceiver(message, type)
//
//            Transaction(
//                id = UUID.randomUUID().toString(),
//                type = type,
//                amount = amount,
//                date = date,
//                reference = reference,
//                balance = balance,
//                description = message,
//                senderReceiver = senderReceiver
//            )
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    private fun determineTransactionType(message: String): TransactionType {
//        return when {
//            message.contains(Regex("(?i)sent to")) -> TransactionType.SEND
//            message.contains(Regex("(?i)received from")) -> TransactionType.RECEIVE
//            message.contains(Regex("(?i)paid to")) -> TransactionType.PAYBILL
//            message.contains(Regex("(?i)Buy Goods")) -> TransactionType.BUY_GOODS
//            message.contains(Regex("(?i)Withdraw")) -> TransactionType.WITHDRAW
//            else -> TransactionType.DEPOSIT
//        }
//    }
//
//    private fun extractAmount(message: String): Double {
//        val match = amountPattern.find(message)
//        return match?.groupValues?.get(1)?.replace(",", "")?.toDouble() ?: 0.0
//    }
//
//    // Implement other extraction methods...
//}