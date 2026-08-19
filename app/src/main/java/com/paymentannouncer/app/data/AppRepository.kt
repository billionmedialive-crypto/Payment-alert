package com.paymentannouncer.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(name = "payment_announcer_settings")

/**
 * Simple app-wide singleton holding settings + in-memory transaction history.
 * History is also persisted to DataStore as JSON so it survives process death,
 * without pulling in a full Room database for a lightweight app like this.
 */
object AppRepository {

    // ---- Live state consumed by the UI ----
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _announcementsEnabled = MutableStateFlow(true)
    val announcementsEnabled: StateFlow<Boolean> = _announcementsEnabled.asStateFlow()

    private val _volume = MutableStateFlow(1.0f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _announceSentToo = MutableStateFlow(false)
    val announceSentToo: StateFlow<Boolean> = _announceSentToo.asStateFlow()

    private val _customPhraseTemplate = MutableStateFlow("{amount} rupees received successfully")
    val customPhraseTemplate: StateFlow<String> = _customPhraseTemplate.asStateFlow()

    private var initialized = false

    private val KEY_ENABLED = booleanPreferencesKey("announcements_enabled")
    private val KEY_VOLUME = floatPreferencesKey("volume")
    private val KEY_RATE = floatPreferencesKey("speech_rate")
    private val KEY_SENT = booleanPreferencesKey("announce_sent")
    private val KEY_PHRASE = stringPreferencesKey("phrase_template")
    private val KEY_HISTORY = stringPreferencesKey("history_json")

    suspend fun init(context: Context) {
        if (initialized) return
        initialized = true
        val prefs = context.dataStore.data.first()
        _announcementsEnabled.value = prefs[KEY_ENABLED] ?: true
        _volume.value = prefs[KEY_VOLUME] ?: 1.0f
        _speechRate.value = prefs[KEY_RATE] ?: 1.0f
        _announceSentToo.value = prefs[KEY_SENT] ?: false
        _customPhraseTemplate.value = prefs[KEY_PHRASE] ?: "{amount} rupees received successfully"

        val historyJson = prefs[KEY_HISTORY]
        if (!historyJson.isNullOrBlank()) {
            _transactions.value = decodeHistory(historyJson)
        }
    }

    suspend fun addTransaction(context: Context, tx: Transaction) {
        val updated = (listOf(tx) + _transactions.value).take(200)
        _transactions.value = updated
        context.dataStore.edit { it[KEY_HISTORY] = encodeHistory(updated) }
    }

    suspend fun clearHistory(context: Context) {
        _transactions.value = emptyList()
        context.dataStore.edit { it[KEY_HISTORY] = "[]" }
    }

    suspend fun setAnnouncementsEnabled(context: Context, enabled: Boolean) {
        _announcementsEnabled.value = enabled
        context.dataStore.edit { it[KEY_ENABLED] = enabled }
    }

    suspend fun setVolume(context: Context, value: Float) {
        _volume.value = value
        context.dataStore.edit { it[KEY_VOLUME] = value }
    }

    suspend fun setSpeechRate(context: Context, value: Float) {
        _speechRate.value = value
        context.dataStore.edit { it[KEY_RATE] = value }
    }

    suspend fun setAnnounceSentToo(context: Context, value: Boolean) {
        _announceSentToo.value = value
        context.dataStore.edit { it[KEY_SENT] = value }
    }

    suspend fun setPhraseTemplate(context: Context, value: String) {
        _customPhraseTemplate.value = value
        context.dataStore.edit { it[KEY_PHRASE] = value }
    }

    private fun encodeHistory(list: List<Transaction>): String {
        val arr = JSONArray()
        list.forEach { tx ->
            val obj = JSONObject()
            obj.put("id", tx.id)
            obj.put("amount", tx.amount)
            obj.put("sourceApp", tx.sourceApp)
            obj.put("rawText", tx.rawText)
            obj.put("timestamp", tx.timestamp)
            obj.put("type", tx.type.name)
            arr.put(obj)
        }
        return arr.toString()
    }

    private fun decodeHistory(json: String): List<Transaction> {
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                Transaction(
                    id = obj.getLong("id"),
                    amount = obj.getDouble("amount"),
                    sourceApp = obj.getString("sourceApp"),
                    rawText = obj.getString("rawText"),
                    timestamp = obj.getLong("timestamp"),
                    type = TransactionType.valueOf(obj.getString("type"))
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
