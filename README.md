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
- versionCode: 2
- versionName: 1.0
- Source of Truth: `main`

## Privacy
Core QR/barcode content is processed on-device and history is stored in app-private storage. Google ML Kit may transmit documented diagnostics/usage telemetry; see `DATA_SAFETY.md` and `PRIVACY_POLICY.md`. The current Internal-test Data Safety declaration is complete for the present dependency set; re-verify it if ads, billing, analytics, backend, account, or other data-transmitting SDKs are added.

## Release gate
See `RELEASE_GATE.md`, `REAL_DEVICE_EVIDENCE.md`, and `CHECKPOINT.md`. Automated build/lint/unit/AAB/16 KB and signed-release gates are recorded as PASS for the current app source. Real-device camera controls, Photo Picker exact-payload decode, current-location/map handoff, WEP fallback, WPA2 request creation, Play App Signing, signed current-main AAB, Data Safety, Ads declaration, target audience, content rating, store assets, Internal testing rollout, and Google Play delivery/update to versionCode 2 on a physical tester device have recorded evidence. Production remains blocked only by the applicable unresolved gates listed in `RELEASE_GATE.md`, including live-camera exact-payload verification, successful authorized Wi-Fi association, the required closed-test duration/count, and monetization work if ads/Ad-Free billing are enabled.


## Monetization plan
- Testing phase: no ads and no billing SDK.
- Planned production release: ads enabled.
- Planned Ad-Free option: one-time in-app purchase to permanently remove ads.
- Play Console Ads/Data Safety/Billing declarations must be updated only when the corresponding production code is actually integrated and verified.
