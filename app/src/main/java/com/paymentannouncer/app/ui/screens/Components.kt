package com.paymentannouncer.app.ui.screens

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paymentannouncer.app.data.Transaction
import com.paymentannouncer.app.data.TransactionType
import com.paymentannouncer.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Big hero balance-style card at the top of the dashboard, subtle gradient + glow. */
@Composable
fun HeroSummaryCard(todayTotal: Double, txCount: Int) {
    val animatedTotal by animateFloatAsState(
        targetValue = todayTotal.toFloat(),
        animationSpec = tween(700, easing = EaseOutCubic),
        label = "totalAnim"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(GradientStart, GradientMid, GradientEnd)
                )
            )
            .border(1.dp, MidnightBorder, RoundedCornerShape(28.dp))
            .padding(24.dp)
    ) {
        Column {
            Text(
                "TODAY'S RECEIVED",
                style = MaterialTheme.typography.labelSmall,
                color = EmeraldPrimary,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "₹",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp, end = 4.dp)
                )
                Text(
                    String.format(Locale.US, "%,.0f", animatedTotal),
                    style = MaterialTheme.typography.displayLarge,
                    color = TextPrimary
                )
            }
            Spacer(Modifier.height(14.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EmeraldPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        "$txCount payments today",
                        style = MaterialTheme.typography.labelLarge,
                        color = EmeraldPrimary
                    )
                }
            }
        }
    }
}

/** Status pill showing whether the listener service is active. */
@Composable
fun ListeningStatusPill(isListening: Boolean, onClick: () -> Unit) {
    val color = if (isListening) SuccessGreen else DangerRed
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MidnightSurfaceElevated)
            .border(1.dp, MidnightBorder, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            if (isListening) "Listening" else "Not active",
            style = MaterialTheme.typography.labelLarge,
            color = TextPrimary
        )
    }
}

@Composable
fun TransactionRow(tx: Transaction) {
    val isReceived = tx.type == TransactionType.RECEIVED
    val accentColor = if (isReceived) SuccessGreen else GoldAccent
    val sdf = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MidnightSurface)
            .border(1.dp, MidnightBorder, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isReceived) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                contentDescription = null,
                tint = accentColor
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                friendlyAppName(tx.sourceApp),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                sdf.format(Date(tx.timestamp)),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        Text(
            (if (isReceived) "+₹" else "-₹") + String.format(Locale.US, "%,.0f", tx.amount),
            style = MaterialTheme.typography.titleLarge,
            color = accentColor,
            fontWeight = FontWeight.Bold
        )
    }
}

fun friendlyAppName(packageName: String): String = when {
    packageName.contains("paisa.user") -> "Google Pay"
    packageName.contains("phonepe") -> "PhonePe"
    packageName.contains("paytm") -> "Paytm"
    packageName.contains("npci") -> "BHIM UPI"
    packageName.contains("whatsapp") -> "WhatsApp Pay"
    packageName.contains("amazon") -> "Amazon Pay"
    else -> packageName.substringAfterLast(".").replaceFirstChar { it.uppercase() }
}
