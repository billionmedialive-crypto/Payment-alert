package com.paymentannouncer.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * The system automatically rebinds NotificationListenerService after boot
 * once permission is granted, so this receiver is mostly a safe no-op hook
 * reserved for any future warm-up logic.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // No-op: NotificationListenerService is restored automatically by the system.
    }
}
