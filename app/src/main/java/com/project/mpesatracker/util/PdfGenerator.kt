package com.project.mpesatracker.util

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import com.project.mpesatracker.data.model.Transaction
import java.io.ByteArrayOutputStream
import java.text.NumberFormat
import java.util.*
import kotlin.math.absoluteValue

class PdfGenerator(private val context: Context) {
    private val paint = Paint().apply {
        textSize = 10f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }
    
    private val boldPaint = Paint().apply {
        textSize = 12f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    
    private val titlePaint = Paint().apply {
        textSize = 16f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val greenPaint = Paint().apply {
        textSize = 10f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        color = Color.rgb(0, 128, 0)
    }

    private val redPaint = Paint().apply {
        textSize = 10f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        color = Color.rgb(200, 0, 0)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateTransactionReport(
        transactions: List<Transaction>,
        totalIncome: Double,
        totalExpenses: Double,
        period: String
    ): ByteArray {
        val document = PdfDocument()
        var currentPage = startNewPage(document)
        var canvas = currentPage.canvas
        var yPosition = 50f

        // Title
        canvas.drawText("M-Pesa Transaction Report", 50f, yPosition, titlePaint)
        yPosition += 30f
        
        canvas.drawText("Period: $period", 50f, yPosition, boldPaint)
        yPosition += 30f

        if (period == "All Time") {
            generateAllTimeReport(document, currentPage, canvas, transactions, totalIncome, totalExpenses)
        } else {
            generateMonthlyReport(document, currentPage, canvas, transactions, totalIncome, totalExpenses, yPosition)
        }

        return ByteArrayOutputStream().use { output ->
            document.writeTo(output)
            document.close()
            output.toByteArray()
        }
    }

    private fun generateAllTimeReport(
        document: PdfDocument,
        initialPage: PdfDocument.Page,
        initialCanvas: android.graphics.Canvas,
        transactions: List<Transaction>,
        totalIncome: Double,
        totalExpenses: Double
    ) {
        var currentPage = initialPage
        var canvas = initialCanvas
        var yPosition = 110f

        // Overall Summary
        canvas.drawText("Overall Summary", 50f, yPosition, boldPaint)
        yPosition += 20f
        canvas.drawText("Total Income: ${formatAmount(totalIncome)}", 50f, yPosition, greenPaint)
        yPosition += 15f
        canvas.drawText("Total Expenses: ${formatAmount(totalExpenses)}", 50f, yPosition, redPaint)
        yPosition += 15f
        canvas.drawText(
            "Net Balance: ${if (totalIncome - totalExpenses >= 0) "+" else "-"}${formatAmount((totalIncome - totalExpenses).absoluteValue)}", 
            50f, 
            yPosition,
            if (totalIncome - totalExpenses >= 0) greenPaint else redPaint
        )
        yPosition += 40f

        // Monthly Breakdown Header
        canvas.drawText("Breakdown Past 12 Months", 50f, yPosition, boldPaint)
        yPosition += 30f

        // Table headers
        val monthX = 50f
        val incomeX = 200f
        val expensesX = 350f
        val netX = 500f

        // Draw table header background
        paint.color = Color.LTGRAY
        canvas.drawRect(monthX - 5f, yPosition - 15f, netX + 45f, yPosition + 5f, paint)
        paint.color = Color.BLACK

        canvas.drawText("Month", monthX, yPosition, boldPaint)
        canvas.drawText("Income", incomeX, yPosition, boldPaint)
        canvas.drawText("Expenses", expensesX, yPosition, boldPaint)
        canvas.drawText("Difference", netX, yPosition, boldPaint)
        yPosition += 20f

        // Group transactions by month and year
        val monthlyData = transactions.groupBy { 
            "${it.timestamp.month.name} ${it.timestamp.year}"
        }.toSortedMap(compareByDescending { it }) // Sort by most recent first

        monthlyData.forEach { (month, monthTransactions) ->
            if (yPosition > 750f) {
                document.finishPage(currentPage)
                currentPage = startNewPage(document)
                canvas = currentPage.canvas
                yPosition = 50f
            }

            val monthlyIncome = monthTransactions.filter { it.amount > 0 }.sumOf { it.amount }
            val monthlyExpenses = monthTransactions.filter { it.amount < 0 }.sumOf { it.amount.absoluteValue }
            val monthlyNet = monthlyIncome - monthlyExpenses

            canvas.drawText(month, monthX, yPosition, paint)
            canvas.drawText("+${formatAmount(monthlyIncome)}", incomeX, yPosition, greenPaint)
            canvas.drawText("-${formatAmount(monthlyExpenses)}", expensesX, yPosition, redPaint)
            canvas.drawText(
                "${if (monthlyNet >= 0) "+" else "-"}${formatAmount(monthlyNet.absoluteValue)}",
                netX,
                yPosition,
                if (monthlyNet >= 0) greenPaint else redPaint
            )

            yPosition += 20f
        }

        document.finishPage(currentPage)
    }

    private fun generateMonthlyReport(
        document: PdfDocument,
        initialPage: PdfDocument.Page,
        initialCanvas: android.graphics.Canvas,
        transactions: List<Transaction>,
        totalIncome: Double,
        totalExpenses: Double,
        startYPosition: Float
    ) {
        var currentPage = initialPage
        var canvas = initialCanvas
        var yPosition = startYPosition

        // Summary
        canvas.drawText("Summary", 50f, yPosition, boldPaint)
        yPosition += 20f
        canvas.drawText("Total Income: +${formatAmount(totalIncome)}", 50f, yPosition, greenPaint)
        yPosition += 15f
        canvas.drawText("Total Expenses: -${formatAmount(totalExpenses)}", 50f, yPosition, redPaint)
        yPosition += 30f

        // Transactions
        canvas.drawText("Transactions", 50f, yPosition, boldPaint)
        yPosition += 20f

        // Table headers
        val dateX = 50f
        val referenceX = 130f
        val typeX = 230f
        val partyX = 300f
        val amountX = 450f
        val balanceX = 520f

        // Draw table header background
        paint.color = Color.LTGRAY
        canvas.drawRect(dateX - 5f, yPosition - 15f, balanceX + 45f, yPosition + 5f, paint)
        paint.color = Color.BLACK

        canvas.drawText("Date", dateX, yPosition, boldPaint)
        canvas.drawText("Reference", referenceX, yPosition, boldPaint)
        canvas.drawText("Type", typeX, yPosition, boldPaint)
        canvas.drawText("Sender/Receiver", partyX, yPosition, boldPaint)
        canvas.drawText("Amount", amountX, yPosition, boldPaint)
        canvas.drawText("Balance", balanceX, yPosition, boldPaint)
        yPosition += 20f

        // Transaction rows
        transactions.forEach { transaction ->
            if (yPosition > 750f) {
                document.finishPage(currentPage)
                currentPage = startNewPage(document)
                canvas = currentPage.canvas
                yPosition = 50f
            }

            val formattedDate = if (true) {
                val day = transaction.timestamp.dayOfMonth
                val suffix = getDayOfMonthSuffix(day)
                val hour = if (transaction.timestamp.hour > 12) 
                    transaction.timestamp.hour - 12 
                else if (transaction.timestamp.hour == 0) 
                    12 
                else 
                    transaction.timestamp.hour
                val minute = transaction.timestamp.minute.toString().padStart(2, '0')
                val amPm = if (transaction.timestamp.hour >= 12) "PM" else "AM"
                "$day$suffix ${hour}:${minute}$amPm"
            } else {
                "${transaction.timestamp.dayOfMonth}/${transaction.timestamp.monthNumber}/${transaction.timestamp.year}"
            }

            val reference = extractReference(transaction.description)
            val (type, party) = extractTypeAndParty(transaction.description)
            
            // Draw date
            canvas.drawText(formattedDate, dateX, yPosition, paint)
            
            // Draw reference
            canvas.drawText(reference, referenceX, yPosition, paint)
            
            // Draw type with color
            val typePaint = if (type == "Received") greenPaint else redPaint
            canvas.drawText(type, typeX, yPosition, typePaint)
            
            // Draw sender/receiver
            canvas.drawText(party, partyX, yPosition, paint)

            // Draw amount with color
            val amountText = formatAmount(transaction.amount.absoluteValue)
            val amountPaint = if (transaction.amount >= 0) greenPaint else redPaint
            canvas.drawText(
                "${if (transaction.amount >= 0) "+" else "-"}$amountText",
                amountX,
                yPosition,
                amountPaint
            )

            // Draw balance
            canvas.drawText(
                formatAmount(transaction.balance),
                balanceX,
                yPosition,
                paint
            )

            yPosition += 15f
        }

        document.finishPage(currentPage)
    }

    private fun extractReference(description: String): String {
        return description.trim().split(Regex("\\s+")).firstOrNull() ?: ""
    }

    private fun extractTypeAndParty(description: String): Pair<String, String> {
        return when {
            description.contains("received") -> {
                val fromIndex = description.indexOf("from") + 5
                val onIndex = description.indexOf(" on ")
                val fullParty = if (onIndex != -1) {
                    description.substring(fromIndex, onIndex).trim()
                } else {
                    description.substring(fromIndex).trim()
                }
                val cleanParty = cleanPartyName(fullParty)
                "Received" to cleanParty
            }
            description.contains("sent to") -> {
                val toIndex = description.indexOf("sent to") + 8
                val forIndex = description.indexOf(" for ")
                val fullParty = if (forIndex != -1) {
                    description.substring(toIndex, forIndex).trim()
                } else {
                    description.substring(toIndex).trim()
                }
                val cleanParty = cleanPartyName(fullParty)
                "Sent" to cleanParty
            }
            description.contains("paid to") -> {
                val toIndex = description.indexOf("paid to") + 8
                val onIndex = description.indexOf(" on ")
                val fullParty = if (onIndex != -1) {
                    description.substring(toIndex, onIndex).trim()
                } else {
                    description.substring(toIndex).trim()
                }
                val cleanParty = cleanPartyName(fullParty)
                "Sent" to cleanParty
            }
            description.contains("transferred to M-Shwari") -> {
                "Sent" to "M-Shwari"
            }
            description.contains("transferred from M-Shwari") -> {
                "Received" to "M-Shwari"
            }
            else -> "Other" to ""
        }
    }

    private fun cleanPartyName(fullParty: String): String {
        // Remove any phone numbers (typically in format 07... or +254...)
        val partyWithoutPhone = fullParty.replace(Regex("\\s+\\d+|\\s+\\+\\d+"), "")
        
        // Split remaining text into words and remove any empty strings
        val names = partyWithoutPhone.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        
        return when {
            names.isEmpty() -> ""
            names.size == 1 -> names[0]
            else -> "${names[0]} ${names[1]}"
        }.trim()
    }

    private fun startNewPage(document: PdfDocument): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        return document.startPage(pageInfo)
    }

    private fun formatAmount(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "KE")).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }
        format.currency = Currency.getInstance("KES")
        
        // Convert to string first to handle thousands
        val amountStr = String.format("%.2f", amount.absoluteValue)
        val amountValue = amountStr.replace(",", "").toDouble()
        
        return format.format(amountValue)
    }

    private fun getDayOfMonthSuffix(n: Int): String = when {
        n in 11..13 -> "th"
        n % 10 == 1 -> "st"
        n % 10 == 2 -> "nd"
        n % 10 == 3 -> "rd"
        else -> "th"
    }
} 