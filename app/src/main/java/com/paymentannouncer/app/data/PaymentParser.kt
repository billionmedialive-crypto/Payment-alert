package com.paymentannouncer.app.data

import java.util.Locale

/**
 * Parses notification text from UPI / payment / banking apps to detect
 * a rupee amount and whether money was received or sent/debited.
 *
 * Works generically across GPay, PhonePe, Paytm, BHIM, WhatsApp Pay,
 * and most bank SMS/notification formats since they all share the
 * same recurring vocabulary ("received", "credited", "debited", "₹", "Rs", "INR").
 */
object PaymentParser {

    // Matches ₹1,234.50 | Rs. 1234 | Rs 500 | INR 999 | 100.00
    private val amountRegex = Regex(
        """(?:₹|Rs\.?|INR)\s?([0-9]+(?:,[0-9]{2,3})*(?:\.[0-9]{1,2})?)""",
        RegexOption.IGNORE_CASE
    )

    private val receivedKeywords = listOf(
        "received", "credited", "credit alert", "you have received",
        "payment received", "money received", "added to your"
    )

    private val sentKeywords = listOf(
        "debited", "sent", "paid", "you paid", "payment of", "withdrawn", "deducted"
    )

    data class ParseResult(val amount: Double, val type: TransactionType)

    fun parse(text: String): ParseResult? {
        val match = amountRegex.find(text) ?: return null
        val cleaned = match.groupValues[1].replace(",", "")
        val amount = cleaned.toDoubleOrNull() ?: return null

        val lower = text.lowercase(Locale.getDefault())
        val isReceived = receivedKeywords.any { lower.contains(it) }
        val isSent = sentKeywords.any { lower.contains(it) }

        val type = when {
            isReceived && !isSent -> TransactionType.RECEIVED
            isSent && !isReceived -> TransactionType.SENT
            isReceived && isSent -> {
                // Ambiguous text containing both; prefer whichever keyword appears first
                val recIdx = receivedKeywords.minOf { k -> lower.indexOf(k).let { if (it < 0) Int.MAX_VALUE else it } }
                val sentIdx = sentKeywords.minOf { k -> lower.indexOf(k).let { if (it < 0) Int.MAX_VALUE else it } }
                if (recIdx <= sentIdx) TransactionType.RECEIVED else TransactionType.SENT
            }
            else -> TransactionType.UNKNOWN
        }

        return ParseResult(amount, type)
    }

    /** Known packages worth listening to. Users can add more from Settings. */
    val defaultTrackedPackages = setOf(
        "com.google.android.apps.nbu.paisa.user", // Google Pay
        "com.phonepe.app",
        "net.one97.paytm",
        "in.org.npci.upiapp", // BHIM
        "com.whatsapp",
        "com.amazon.mShop.android.shopping" // Amazon Pay notifications
    )
}
