# QR Scanners — Play Console Answer Draft

Source of Truth: `main`. This file is a source-backed draft for Play Console entry. It does **not** mean the corresponding Play Console form has already been submitted.

## App identity
- App name: **QR Scanners**
- Package: `com.anakinyoo.qrscanners`
- Version: `1.0`
- Developer: **AnakinYoo**
- Privacy policy URL: https://raw.githubusercontent.com/dachopol/QR-Scanners/main/PRIVACY_POLICY.md

## App access
**Recommended answer from current source:** All app functionality is available without login, membership, subscription, or special reviewer credentials.

Evidence:
- No account/login implementation.
- No developer backend.
- No gated paid functionality in the current release source.

## Ads
**Recommended answer from current source:** No — the current release source contains no advertising SDK.

Recheck before submission if dependencies change.

## Data Safety
Do **not** answer “No data collected.”

Current source uses `com.google.mlkit:barcode-scanning`. Google documents ML Kit Android SDK collection for diagnostics/usage analytics, including device/app information, per-installation or device identifiers, performance metrics, API configuration, feature input/output size, feature version, event types, and error codes.

Google states the listed ML Kit data is encrypted in transit and is not transferred to third parties.

Barcode auto-zoom is not enabled in this source, so the additional auto-zoom session id/zoom/bounding-box telemetry is not expected.

### User content handled locally by QR Scanners
- Camera frames: processed for scanning; not intentionally recorded or uploaded by the app.
- Photo Picker images: selected by the user and processed for barcode detection.
- Scan/generated payloads, favorites, settings and history: stored in app-private local storage.
- Optional foreground location: used only to fill Location QR coordinates when the user requests it.
- No background-location permission.
- Android backup is disabled.

### Final Data Safety entry
TO VERIFY in Play Console immediately before release because the form’s exact data-type labels/definitions can change. Review the final release dependency tree and current Play Console wording before submitting.

## Account deletion
**Not applicable from current source:** QR Scanners does not provide user accounts.

## Permissions / sensitive access
- Camera: optional runtime permission for live scanning.
- Photos/media: Android Photo Picker; no broad media-library permission.
- Location: optional foreground coarse/fine location for filling a Location QR.
- Background location: not requested.
- Internet/network state: used by platform/SDK/system network flows documented in the app.

## Content rating
TO VERIFY in the actual Play Console questionnaire. Answer only from the shipping app behavior; do not infer or pre-fill age/content answers outside the current questionnaire.

## Target audience
TO VERIFY in Play Console. The source does not establish a children-directed product claim. Do not select child-directed categories by assumption.

## Store assets ready
- Play icon: `store-assets/play_store_icon_512.png`
- Feature graphic: `store-assets/feature_graphic_1024x500.jpg`
- Thai phone screenshots: `store-assets/screenshots/th/`

## Still not complete until account-side evidence exists
- Real upload-key signing evidence.
- Data Safety form submitted against the final release dependency tree.
- Ads declaration submitted.
- Content rating completed.
- Target audience completed.
- Testing-track / production-access requirements confirmed for the actual developer account.
- Signed AAB uploaded from the same verified release commit.
