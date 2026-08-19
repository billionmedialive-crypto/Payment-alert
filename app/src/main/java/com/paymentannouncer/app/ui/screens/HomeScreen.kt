package com.paymentannouncer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.paymentannouncer.app.data.Transaction
import com.paymentannouncer.app.data.TransactionType
import com.paymentannouncer.app.ui.theme.*
import java.util.Calendar

@Composable
fun HomeScreen(
    transactions: List<Transaction>,
    isListening: Boolean,
    announcementsEnabled: Boolean,
    onToggleAnnouncements: (Boolean) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenNotificationSettings: () -> Unit
) {
    val todayStart = remember(transactions) {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
        }.timeInMillis
    }
    val todaysTx = transactions.filter { it.timestamp >= todayStart && it.type == TransactionType.RECEIVED }
    val todayTotal = todaysTx.sumOf { it.amount }

    Scaffold(
        containerColor = MidnightBg,
        topBar = {
            TopAppBar(
                title = { Text("Payment Announcer", fontWeight = FontWeight.Bold, color = TextPrimary) },
                actions = {
                    IconButton(onClick = { onToggleAnnouncements(!announcementsEnabled) }) {
                        Icon(
                            if (announcementsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Toggle announcements",
                            tint = if (announcementsEnabled) EmeraldPrimary else NeutralSlate
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MidnightBg)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MidnightBg)
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item { Spacer(Modifier.height(4.dp)) }

            item { HeroSummaryCard(todayTotal = todayTotal, txCount = todaysTx.size) }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ListeningStatusPill(isListening = isListening, onClick = onOpenNotificationSettings)
                    if (!isListening) {
                        TextButton(onClick = onOpenNotificationSettings) {
                            Text("Enable", color = EmeraldPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item {
                Text(
                    "RECENT ACTIVITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeutralSlate
                )
            }

            if (transactions.isEmpty()) {
                item { EmptyState() }
            } else {
                items(transactions, key = { it.id }) { tx ->
                    TransactionRow(tx)
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "No payments yet",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Received payments will appear here automatically",
            style = MaterialTheme.typography.bodyMedium,
            color = NeutralSlate
        )
    }
}
