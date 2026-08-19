package com.paymentannouncer.app.data

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

/**
 * Wraps Android's TextToSpeech engine to loudly announce received payments,
 * e.g. "100 rupees received successfully".
 */
class SpeechAnnouncer(context: Context) {

    private var tts: TextToSpeech? = null
    private var ready = false
    private val pendingQueue = mutableListOf<String>()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("en", "IN")
                ready = true
                pendingQueue.forEach { speakInternal(it) }
                pendingQueue.clear()
            }
        }
    }

    fun setVolumePlaceholder(volume: Float) {
        // Volume is applied per-utterance via params in speak()
        this.currentVolume = volume.coerceIn(0f, 1f)
    }

    fun setRate(rate: Float) {
        tts?.setSpeechRate(rate.coerceIn(0.5f, 2.0f))
    }

    private var currentVolume: Float = 1.0f

    fun speak(text: String) {
        if (!ready) {
            pendingQueue.add(text)
            return
        }
        speakInternal(text)
    }

    private fun speakInternal(text: String) {
        val params = HashMap<String, String>()
        params[TextToSpeech.Engine.KEY_PARAM_VOLUME] = currentVolume.toString()
        params[TextToSpeech.Engine.KEY_PARAM_STREAM] = android.media.AudioManager.STREAM_MUSIC.toString()
        @Suppress("DEPRECATION")
        tts?.speak(text, TextToSpeech.QUEUE_ADD, params, "payment_announcement_${System.currentTimeMillis()}")
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}

/** Builds the spoken phrase, e.g. "100 rupees received successfully". */
fun buildAnnouncementPhrase(template: String, amount: Double): String {
    val amountText = if (amount == amount.toLong().toDouble()) {
        amount.toLong().toString()
    } else {
        String.format(Locale.US, "%.2f", amount)
    }
    return template.replace("{amount}", amountText)
}
