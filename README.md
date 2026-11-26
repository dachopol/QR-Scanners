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
- versionCode: 3
- versionName: 1.0
- Source of Truth: `main`

## Privacy
Core QR/barcode content is processed on-device and history is stored in app-private storage. Google ML Kit may transmit documented diagnostics/usage telemetry; see `DATA_SAFETY.md` and `PRIVACY_POLICY.md`. The current Internal-test Data Safety declaration is complete for the present dependency set; re-verify it if ads, billing, analytics, backend, account, or other data-transmitting SDKs are added.

## Release gate
See `RELEASE_GATE.md`, `REAL_DEVICE_EVIDENCE.md`, and `CHECKPOINT.md`. Automated build/lint/unit/AAB/16 KB and signed-release gates are recorded as PASS for the current app source. Real-device camera controls, live-camera exact-payload decode, Photo Picker exact-payload decode, current-location/map handoff, WEP fallback, WPA2 request creation and successful WPA2 association, Play App Signing, signed current-main AAB, Data Safety, Ads declaration, target audience, content rating, store assets, Internal testing rollout, and Google Play delivery/update to versionCode 2 on a physical tester device have recorded evidence. Production remains blocked only by the applicable unresolved gates listed in `RELEASE_GATE.md`, including the required closed-test duration/count, monetization work if ads/Ad-Free billing are enabled, and tester-client listing-icon re-verification after Play review.


## Monetization plan
- Testing phase: no ads and no billing SDK.
- Planned production release: ads enabled.
- Planned Ad-Free option: one-time in-app purchase to permanently remove ads.
- Play Console Ads/Data Safety/Billing declarations must be updated only when the corresponding production code is actually integrated and verified.


## UCISE Local — คู่มือเริ่มต้นและ Documentation Gate / Getting started

<!-- UCISE_DOCUMENTATION_GATE_V1 -->

**ขั้นตอนเริ่มต้น (TH):** อ่าน Core features, Project identity และ Privacy → ตรวจ Android SDK และ build entry point จาก source จริง → อนุญาตกล้องหรือเลือกรูปเท่าที่จำเป็น → ทดสอบสแกน QR/บาร์โค้ดและสร้าง QR ด้วยข้อมูลทดสอบที่ไม่เป็นความลับ → ตรวจผลและ permission denial → ตรวจ Release gate แยกจากผล source.

**Getting started (EN):** Read the project-specific sections above. Confirm exact version, authorized workspace, platform and prerequisites before following any command. Run only documented, authorized steps; record actual output and errors. Do not infer runtime or release readiness from this documentation.

### Required documentation before closing work

- [ ] Purpose, exact version/source identity, prerequisites and installation/configuration.
- [ ] Step-by-step user workflow, examples, expected outputs, and TH/EN guidance appropriate to the audience.
- [ ] Build/run/test commands from actual source where applicable; record host, exact command, exit status and evidence. Untested commands are NOT_VERIFIED, not PASS.
- [ ] Architecture, permissions, safety constraints, troubleshooting, recovery/rollback and limitations.
- [ ] AI handoff: checkpoint, changed files, remaining tasks, dependency and next authorized action; research assumptions and evidence scope when applicable.
- [ ] Review existing documentation first; update it without duplication; never copy secrets or local-only data to the cloud.

Use README sections or existing linked guides; split into QUICKSTART, USER_GUIDE, AGENTS, TESTING, SECURITY, RESEARCH or RECOVERY files only when useful. Missing required documentation or evidence keeps the relevant DoD item INCOMPLETE/BLOCKED. This checklist is not a test receipt, native approval, release authorization or permission to bypass Owner/Watchdog Hold. Production and existing security gates remain unchanged. Documentation-only candidate; command execution and independent QA are NOT_RUN in this edit.
