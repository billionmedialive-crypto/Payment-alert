package com.paymentannouncer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.paymentannouncer.app.ui.theme.*

@Composable
fun OnboardingScreen(onGrantAccessClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBg)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(EmeraldPrimary, EmeraldDeep))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Campaign,
                contentDescription = null,
                tint = MidnightBg,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(Modifier.height(28.dp))

        Text(
            "Payment Announcer",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Announce every payment out loud, the moment it lands — \"100 rupees received successfully.\"",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        StepCard(number = "1", text = "Grant notification access so the app can detect payment alerts from GPay, PhonePe, Paytm & more")
        Spacer(Modifier.height(12.dp))
        StepCard(number = "2", text = "Keep the app running in the background — no need to open it")
        Spacer(Modifier.height(12.dp))
        StepCard(number = "3", text = "Every payment received is announced loudly and logged in your history")

        Spacer(Modifier.height(36.dp))

        Button(
            onClick = onGrantAccessClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EmeraldPrimary,
                contentColor = MidnightBg
            )
        ) {
            Text("Grant Notification Access", fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(12.dp))
        Text(
            "Your data stays on your device. Nothing is uploaded.",
            style = MaterialTheme.typography.bodyMedium,
            color = NeutralSlate,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StepCard(number: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MidnightSurface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(GoldAccent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Text(number, color = GoldAccent, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(14.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}
