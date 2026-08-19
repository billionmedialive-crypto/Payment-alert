package com.paymentannouncer.app

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.paymentannouncer.app.data.AppRepository
import com.paymentannouncer.app.data.PaymentParser
import com.paymentannouncer.app.data.SpeechAnnouncer
import com.paymentannouncer.app.data.Transaction
import com.paymentannouncer.app.data.TransactionType
import com.paymentannouncer.app.data.buildAnnouncementPhrase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PaymentNotificationListener : NotificationListenerService() {

    private lateinit var announcer: SpeechAnnouncer
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        announcer = SpeechAnnouncer(applicationContext)
        runBlocking { AppRepository.init(applicationContext) }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val title = extras.getCharSequence("android.title")?.toString().orEmpty()
        val text = extras.getCharSequence("android.text")?.toString().orEmpty()
        val bigText = extras.getCharSequence("android.bigText")?.toString().orEmpty()
        val combined = listOf(title, text, bigText).filter { it.isNotBlank() }.joinToString(" | ")

        if (combined.isBlank()) return

        val result = PaymentParser.parse(combined) ?: return
        if (result.type == TransactionType.UNKNOWN) return

        scope.launch {
            val enabled = AppRepository.announcementsEnabled.value
            val announceSent = AppRepository.announceSentToo.value

            val tx = Transaction(
                amount = result.amount,
                sourceApp = sbn.packageName,
                rawText = combined,
                type = result.type
            )
            AppRepository.addTransaction(applicationContext, tx)

            val shouldSpeak = enabled && (result.type == TransactionType.RECEIVED ||
                    (result.type == TransactionType.SENT && announceSent))

            if (shouldSpeak) {
                val template = if (result.type == TransactionType.RECEIVED) {
                    AppRepository.customPhraseTemplate.value
                } else {
                    "{amount} rupees sent"
                }
                announcer.setVolumePlaceholder(AppRepository.volume.value)
                announcer.setRate(AppRepository.speechRate.value)
                announcer.speak(buildAnnouncementPhrase(template, result.amount))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        announcer.shutdown()
    }
}
