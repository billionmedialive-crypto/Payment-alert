package com.paymentannouncer.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.paymentannouncer.app.data.buildAnnouncementPhrase
import com.paymentannouncer.app.ui.theme.*

@Composable
fun SettingsScreen(
    phraseTemplate: String,
    onPhraseChange: (String) -> Unit,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    speechRate: Float,
    onSpeechRateChange: (Float) -> Unit,
    announceSentToo: Boolean,
    onAnnounceSentToggle: (Boolean) -> Unit,
    onTestAnnouncement: () -> Unit,
    onClearHistory: () -> Unit,
    onOpenNotificationAccess: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = MidnightBg,
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MidnightBg)
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            SectionCard(title = "Announcement phrase") {
                OutlinedTextField(
                    value = phraseTemplate,
                    onValueChange = onPhraseChange,
                    label = { Text("Use {amount} as placeholder") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = MidnightBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Preview: \"${buildAnnouncementPhrase(phraseTemplate, 100.0)}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EmeraldPrimary
                )
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onTestAnnouncement,
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, EmeraldPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Test announcement", color = EmeraldPrimary)
                }
            }

            SectionCard(title = "Voice") {
                Text("Volume", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Slider(
                    value = volume,
                    onValueChange = onVolumeChange,
                    colors = SliderDefaults.colors(
                        thumbColor = EmeraldPrimary,
                        activeTrackColor = EmeraldPrimary
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text("Speech rate", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Slider(
                    value = speechRate,
                    onValueChange = onSpeechRateChange,
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = EmeraldPrimary,
                        activeTrackColor = EmeraldPrimary
                    )
                )
            }

            SectionCard(title = "Behaviour") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Also announce money sent", color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "By default only received payments are announced",
                            color = NeutralSlate,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Switch(
                        checked = announceSentToo,
                        onCheckedChange = onAnnounceSentToggle,
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary, checkedTrackColor = EmeraldDeep)
                    )
                }
            }

            SectionCard(title = "Permissions") {
                Text(
                    "Notification access lets the app detect payment alerts. If announcements stop working, re-check this.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onOpenNotificationAccess,
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, MidnightBorder),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Open notification access settings", color = TextPrimary)
                }
            }

            SectionCard(title = "Data") {
                OutlinedButton(
                    onClick = onClearHistory,
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, DangerRed),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Clear history", color = DangerRed)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MidnightSurface, RoundedCornerShape(20.dp))
            .border(BorderStroke(1.dp, MidnightBorder), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(14.dp))
        content()
    }
}
