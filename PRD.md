# QR Scanners — Product Requirements Baseline

Status: current implemented/release baseline. Requirements below are traceable to the current source, tests and release documents.

## Product goal

Provide an Android QR/barcode scanner and QR generator that is fast to operate, privacy-conscious, bilingual (TH/EN), and releasable through Google Play with evidence-backed quality gates.

## Functional requirements

### Scan
- Live camera scan using CameraX + ML Kit.
- Support all barcode formats exposed by the configured ML Kit scanner.
- Debounce repeated live detections.
- Pause live analysis while the result sheet is open.
- Scan a user-selected image using Android Photo Picker without broad media-library permission.
- Show decoded payload and detected format/type.

### Scan actions
- URL: open browser.
- Wi-Fi: parse standard Wi-Fi QR payload; request supported Android connection flow or fall back to Wi-Fi Settings without false-success messaging.
- Contact: open add-contact flow.
- Phone: open dialer.
- SMS: open SMS flow.
- Email: open email flow.
- Geo: open map/browser flow.
- Text: copy/share/search where applicable.

### QR generation
Generate QR content for:
- Text
- URL
- Wi-Fi
- Contact
- Email
- Phone
- SMS
- Geo

Optional current-location fill may request foreground coarse/fine location only.

### History and privacy
- Store history in app-private local storage.
- Support favorites, search, delete/clear and CSV export.
- Use atomic persistence for history.
- Hide/redact Wi-Fi credentials in history previews and CSV export.
- Mark copied Wi-Fi passwords as sensitive on supported Android versions.
- Disable Android app backup/device-transfer for app data.

### UI/UX
- Compact mobile layout with bottom navigation.
- Wide layout with navigation rail.
- Minimum practical touch targets for quick actions.
- TH/EN/System language selection.
- Light/Dark/System theme selection.
- No hard-coded visible UI strings where localized resources are required.

## Non-functional requirements

- targetSdk 36.
- minSdk 24.
- Signed release APK/AAB pipeline.
- 16 KB alignment gate.
- No fake/random production state or fabricated test evidence.
- No developer backend/account/ads/billing unless explicitly added and re-reviewed.
- ML Kit telemetry must be represented accurately in Privacy Policy and Data Safety.

## Release requirements

- Current source must pass unit tests, lint, Debug/Release builds, AAB build, signing verification and 16 KB alignment.
- Current code version must receive targeted physical-device regression.
- Data Safety, privacy policy, ads declaration, content rating, target audience and store assets must match actual build behavior.
- Eligible personal developer account must complete required closed testing before Production Access.
- If production ads/Ad-Free are enabled, only real AdMob/Play Billing configuration may be used, followed by purchase/restore/ad-suppression and Play declaration re-verification.

## Acceptance evidence

Primary evidence sources:
- `RELEASE_GATE.md`
- `REAL_DEVICE_EVIDENCE.md`
- `SIGNING_EVIDENCE.md`
- `DATA_SAFETY.md`
- `TEST_PLAN.md`
- GitHub Actions Android Release Gate
