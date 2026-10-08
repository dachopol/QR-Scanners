# QR Scanners — Dependency Audit (2026-10-09)

Scope: dependencies that materially affect the current QR/barcode core flow.

## Google ML Kit barcode scanning

Current source:
- `com.google.mlkit:barcode-scanning:17.3.0`

Official Google documentation currently lists `17.3.0` as the bundled Barcode Scanning dependency. No change is required.

Reference:
- https://developers.google.com/ml-kit/vision/barcode-scanning/android
- https://developers.google.com/ml-kit/release-notes

Decision: **KEEP 17.3.0**.

Reason:
- Bundled model is immediately available offline.
- Current app depends on custom CameraX UI/analysis rather than the simpler Google Code Scanner flow.
- No evidence supports changing ML Kit only for version freshness.

## CameraX

Previous source:
- `androidx.camera:camera-*:1.4.1`

Official AndroidX stable channel lists CameraX `1.6.2` (26 Aug 2026). CameraX 1.6 release notes include fixes relevant to newer Android versions, including crashes on certain Android 17+ devices caused by previously unknown dynamic-range profiles.

Reference:
- https://developer.android.com/jetpack/androidx/releases/camera
- https://developer.android.com/jetpack/androidx/versions/stable-channel

Decision: **UPDATE to 1.6.2**.

Change:
- `gradle/libs.versions.toml`: `camera = "1.6.2"`

Verification requirements:
1. Android Release Gate must pass unit/lint/Debug/Release/AAB/16 KB/signing.
2. Physical regression must re-check:
   - rear/front camera bind
   - live barcode decode
   - torch
   - zoom
   - lifecycle cleanup / re-entry
3. Android 17 emulator/device should be included when an authorized runtime channel is available.

## Broad dependency upgrades

No broad Compose/Kotlin/AndroidX version sweep is authorized by this audit. Release hardening should avoid unrelated dependency churn unless there is a demonstrated defect, compatibility requirement, or security reason.

Rule: newest is not automatically better; upgrade only with evidence and regression coverage.
