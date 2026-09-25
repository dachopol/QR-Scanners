# QR Scanners

Android QR & barcode scanner + generator by AnakinYoo, built with Kotlin and Jetpack Compose.

## Core features
- CameraX + ML Kit real-time QR/barcode scanning.
- Android Photo Picker gallery scan.
- Flash, zoom, front/rear camera controls.
- Result actions for URL, Wi-Fi, contact, phone, SMS, email, and geo content.
- QR generation for Text, URL, Wi-Fi, Contact, Email, Phone, SMS, and Geo.
- Location QR can auto-fill the current device location with foreground permission; manual coordinates remain available.
- Local history, favorites, search, delete/clear, CSV export.
- Dark / Light / System theme and Thai / English / System language.
- Splash screen with app icon, QR Scanners, and by AnakinYoo.

## Project identity
- Package / Application ID: `com.anakinyoo.qrscanners`
- targetSdk: 36
- compileSdk: Android 16 QPR2 SDK 36.1
- versionCode: 1
- versionName: 1.0
- Source of Truth: `main`

## Privacy
Core QR/barcode content is processed on-device and history is stored in app-private storage. Google ML Kit may transmit documented diagnostics/usage telemetry; see `DATA_SAFETY.md` and `PRIVACY_POLICY.md`. Do not describe the release as “no data collected” until the final Play Console Data Safety review is complete.

## Release gate
See `RELEASE_GATE.md` and `REAL_DEVICE_EVIDENCE.md`. Automated build/lint/unit/AAB/16 KB gates pass on main. Real-device cold launch, CameraX, torch, zoom, and front/rear camera switching have been verified. Gallery decode completion, current-location/map handoff, Wi-Fi behavior, real release signing, Play Console forms, and required testing still need real evidence before Production.
