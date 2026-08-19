package com.paymentannouncer.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.paymentannouncer.app.data.AppRepository
import com.paymentannouncer.app.data.SpeechAnnouncer
import com.paymentannouncer.app.data.Transaction
import com.paymentannouncer.app.data.TransactionType
import com.paymentannouncer.app.data.buildAnnouncementPhrase
import com.paymentannouncer.app.ui.screens.HomeScreen
import com.paymentannouncer.app.ui.screens.OnboardingScreen
import com.paymentannouncer.app.ui.screens.SettingsScreen
import com.paymentannouncer.app.ui.theme.PaymentAnnouncerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var testAnnouncer: SpeechAnnouncer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        testAnnouncer = SpeechAnnouncer(applicationContext)

        setContent {
            PaymentAnnouncerTheme {
                AppRoot(testAnnouncer = testAnnouncer)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        testAnnouncer.shutdown()
    }
}

private fun isNotificationAccessGranted(context: android.content.Context): Boolean {
    val enabledListeners = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
    return enabledListeners?.contains(context.packageName) == true
}

@Composable
fun AppRoot(testAnnouncer: SpeechAnnouncer) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    var hasNotificationAccess by remember { mutableStateOf(isNotificationAccessGranted(context)) }

    // Re-check permission whenever the app resumes (e.g. returning from system settings)
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                hasNotificationAccess = isNotificationAccessGranted(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        AppRepository.init(context)
    }

    val transactions by AppRepository.transactions.collectAsStateWithLifecycle()
    val announcementsEnabled by AppRepository.announcementsEnabled.collectAsStateWithLifecycle()
    val volume by AppRepository.volume.collectAsStateWithLifecycle()
    val speechRate by AppRepository.speechRate.collectAsStateWithLifecycle()
    val announceSentToo by AppRepository.announceSentToo.collectAsStateWithLifecycle()
    val phraseTemplate by AppRepository.customPhraseTemplate.collectAsStateWithLifecycle()

    fun openNotificationAccessSettings() {
        context.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
    }

    if (!hasNotificationAccess) {
        OnboardingScreen(onGrantAccessClick = { openNotificationAccessSettings() })
        return
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                transactions = transactions,
                isListening = hasNotificationAccess,
                announcementsEnabled = announcementsEnabled,
                onToggleAnnouncements = { enabled ->
                    scope.launch { AppRepository.setAnnouncementsEnabled(context, enabled) }
                },
                onOpenSettings = { navController.navigate("settings") },
                onOpenNotificationSettings = { openNotificationAccessSettings() }
            )
        }
        composable("settings") {
            SettingsScreen(
                phraseTemplate = phraseTemplate,
                onPhraseChange = { scope.launch { AppRepository.setPhraseTemplate(context, it) } },
                volume = volume,
                onVolumeChange = { scope.launch { AppRepository.setVolume(context, it) } },
                speechRate = speechRate,
                onSpeechRateChange = { scope.launch { AppRepository.setSpeechRate(context, it) } },
                announceSentToo = announceSentToo,
                onAnnounceSentToggle = { scope.launch { AppRepository.setAnnounceSentToo(context, it) } },
                onTestAnnouncement = {
                    testAnnouncer.setVolumePlaceholder(volume)
                    testAnnouncer.setRate(speechRate)
                    testAnnouncer.speak(buildAnnouncementPhrase(phraseTemplate, 100.0))
                },
                onClearHistory = { scope.launch { AppRepository.clearHistory(context) } },
                onOpenNotificationAccess = { openNotificationAccessSettings() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
