package com.project.mpesatracker.data.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import com.project.mpesatracker.data.model.Transaction
import com.project.mpesatracker.util.MpesaSmsParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TransactionRepository(
    private val context: Context,
    private val smsParser: MpesaSmsParser
) {
    fun getTransactions(): Flow<List<Transaction>> = flow {
        val transactions = readMpesaSms()
        emit(transactions)
    }.flowOn(Dispatchers.IO)

    private fun readMpesaSms(): List<Transaction> {
        val messages = mutableListOf<Transaction>()
        val cursor = context.contentResolver.query(
            Uri.parse("content://sms/inbox"),
            arrayOf(
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE
            ),
            "${Telephony.Sms.ADDRESS} LIKE ?",
            arrayOf("MPESA"),
            "${Telephony.Sms.DATE} DESC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val message = readSmsMessage(it)
                smsParser.parseMessage(message)?.let { transaction ->
                    messages.add(transaction)
                }
            }
        }

        return messages
    }

    private fun readSmsMessage(cursor: Cursor): String {
        val messageBody = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))
        return messageBody
    }

    suspend fun clearAllTransactions() {
        // Implement if you want to store transactions in a local database
    }
} 