# Payment Announcer

A premium-styled Android app that loudly announces every payment you receive —
e.g. **"100 rupees received successfully"** — the instant a GPay / PhonePe / Paytm /
BHIM / bank notification arrives, even in the background.

## How it works

1. **NotificationListenerService** (`PaymentNotificationListener.kt`) reads incoming
   notifications system-wide (requires one-time user permission — this is how every
   "sound box" payment app works, since UPI apps don't broadcast payment events directly).
2. **PaymentParser.kt** scans the notification text with regex for `₹ / Rs / INR` amounts
   and keywords like "received", "credited" vs. "debited", "sent".
3. **SpeechAnnouncer.kt** uses Android's built-in `TextToSpeech` engine to speak the
   amount out loud — no internet or third-party API required.
4. Every detected payment is saved locally (DataStore, on-device only — nothing is
   ever uploaded) and shown in a scrollable history on the home screen.

## Design

Dark "fintech" theme — deep midnight navy background, emerald-green accent for money,
gold accent for premium touches. Built entirely in **Jetpack Compose + Material 3**:
- Animated hero card showing today's total received
- Live "Listening" status pill
- Card-based transaction history with per-app icons
- Settings screen: customize the exact spoken phrase (`{amount}` placeholder),
  volume, speech rate, and whether "sent" payments are announced too

## How to build

1. Install **Android Studio** (Koala or newer).
2. Open this folder (`PaymentAnnouncer/`) as a project — Android Studio will detect
   it as a Gradle project automatically.
3. Let Gradle sync (it will download the wrapper + dependencies on first run).
4. Click **Run ▶** with a device/emulator connected (minSdk 26 / Android 8.0+).

No API keys, no backend, no paid services needed — everything runs on-device.

## First-time setup on the phone

1. Launch the app → tap **"Grant Notification Access"** → find **Payment Announcer**
   in the list → toggle it on. (This is a standard Android permission screen; the
   app cannot do this automatically for security reasons.)
2. Return to the app — you'll see the dashboard with a green **"Listening"** status.
3. Keep the app installed (it doesn't need to stay open) — battery optimization
   should be disabled for it in phone Settings so Android doesn't kill it.

## Customizing the announcement

Go to **Settings → Announcement phrase**. Default is:

```
{amount} rupees received successfully
```

`{amount}` is replaced with the parsed number, e.g. **"100 rupees received successfully"**.
You can change wording, add your shop name, etc. — e.g.
`"{amount} rupees received. Thank you!"`

## Notes & limitations

- Some payment apps show notifications only after unlock, or batch them — the app
  reacts to whatever notification text Android delivers, so it inherits that timing.
- Certain OEMs (Xiaomi, Oppo, Vivo) aggressively kill background services — disable
  battery optimization for the app under phone Settings → Apps → Payment Announcer → Battery.
- The regex parser covers the common notification formats from GPay, PhonePe, Paytm,
  BHIM, WhatsApp Pay, and most bank alerts. If a specific bank's wording isn't caught,
  the keyword/regex lists in `PaymentParser.kt` are easy to extend.
