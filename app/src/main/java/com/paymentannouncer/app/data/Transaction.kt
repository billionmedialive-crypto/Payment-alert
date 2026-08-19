package com.paymentannouncer.app.data

data class Transaction(
    val id: Long = System.currentTimeMillis(),
    val amount: Double,
    val sourceApp: String,
    val rawText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: TransactionType = TransactionType.RECEIVED
)

enum class TransactionType { RECEIVED, SENT, UNKNOWN }
