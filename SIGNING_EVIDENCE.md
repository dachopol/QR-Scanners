# QR Scanners — Signing Evidence

Package under release: `com.anakinyoo.qrscanners`

## Evidence collected

### Installed physical-device build
- Package: `com.anakinyoo.qrscanners`
- Installed build signer: Android Debug certificate
- SHA-256: `6fdf8aac7bc7b80ad2ade6235454fdde62323fa3935cde84511a285a96ecca8f`
- This is a debug/test signature and is not release-signing evidence.

### Existing QuickQR signing material
A local signing set named **QuickQR Business** was found and inspected read-only.

- Certificate subject: `CN=AnakinYoo, OU=QuickQR Business, O=AnakinYoo, C=TH`
- Certificate SHA-256: `7999f57e361ef13c2ab5f88763b58ca7b67f58de18263366e63fc5779a738301`
- Existing signed bundle: `QuickQR-Business-v17-signed.aab`
- The bundle signer matches the QuickQR Business certificate.
- Bundle manifest, read with Google bundletool, reports:
  - package: `com.aistudio.qrgenerator.kmpzqr`
  - versionCode: `17`
  - versionName: `17.0`

## Decision

The QuickQR Business key belongs to evidence for a **different package**. It must not be reused for `com.anakinyoo.qrscanners` by assumption.

Play Console evidence collected on 2026-09-26 confirms that the **QR Scanners** app exists for package `com.anakinyoo.qrscanners`, **Play App Signing** is active, and Google Play manages the app-signing key. The first signed AAB was subsequently accepted by Play Console, and the upload-certificate SHA-1/SHA-256 shown by Play exactly match the dedicated QR Scanners upload certificate recorded below.

## Dedicated QR Scanners upload key — created 2026-09-26

- Alias: `qr-scanners-upload`
- Certificate subject: `CN=AnakinYoo, OU=QR Scanners, O=AnakinYoo, C=TH`
- Algorithm: RSA 4096 / SHA256withRSA
- Validity: 2026-09-26 through 2054-02-11
- SHA-1: `CA:D7:57:8B:8E:81:77:46:43:BA:BA:8B:E1:40:A8:65:56:04:76:F4`
- SHA-256: `E7:47:7E:26:10:51:E5:56:3A:6F:51:FF:FE:00:10:04:55:00:14:19:2F:BB:FE:04:93:11:74:F8:F9:1A:B6:40`
- Keystore is stored outside the repository on the authorized Windows machine.
- Password material is stored with Windows DPAPI for the current Windows user; plaintext was not written to repo or emitted to chat.
- Repository scan confirmed no `.jks`, `.keystore`, `.p12`, `.pfx`, or upload-password file is committed.

## Signing status

- CI signing secrets are configured and signature verification has passed for the recorded release APK/AAB.
- The first signed AAB was accepted by Play Console and the upload-certificate fingerprints match the dedicated QR Scanners upload key.
- Continue to keep keystore files, passwords, base64 keystore material, and secret values outside the repository.


## First signed AAB / Play acceptance — 2026-09-26

- GitHub Actions signing secrets configured: `KEYSTORE_BASE64`, `STORE_PASSWORD`, `KEY_PASSWORD`, `KEY_ALIAS`.
- Signed CI run: `36222084823`.
- Release source commit: `cbbe5e462110bd0a273cb4eda1111e8d85c800fb`.
- CI signing verification: PASS for release APK and AAB.
- First signed AAB uploaded successfully to Google Play Console Internal testing release draft.
- Bundle accepted as versionCode `1`, versionName `1.0`, min API 24+, target SDK 36.
- Google Play upload-certificate SHA-1: `CA:D7:57:8B:8E:81:77:46:43:BA:BA:8B:E1:40:A8:65:56:04:76:F4`.
- Google Play upload-certificate SHA-256: `E7:47:7E:26:10:51:E5:56:3A:6F:51:FF:FE:00:10:04:55:00:14:19:2F:BB:FE:04:93:11:74:F8:F9:1A:B6:40`.
- Both Play fingerprints exactly match the dedicated local QR Scanners upload certificate.
- Internal testing rollout was subsequently published and is recorded in `RELEASE_GATE.md` / `PLAY_CONSOLE_CHECKLIST.md`.

## Current-main v2 signed AAB / Play delivery — 2026-09-27

- Source commit: `c2ed27f85a1109d12bb04dd06443344f7ca88726`.
- Version: versionCode `2`, versionName `1.0`.
- GitHub Actions Android Release Gate: run `36294091474` — PASS for build, release signing, 16 KB alignment and release-evidence upload.
- CI artifact ID: `10923177572`; artifact ZIP integrity test passed before use.
- Extracted release AAB signer: `CN=AnakinYoo, OU=QR Scanners, O=AnakinYoo, C=TH`; local `jarsigner -verify` reported `jar verified.`.
- Google Play accepted the bundle as `2 (1.0)`, min API 24+, target SDK 36, and published it to Internal testing.
- Xiaomi `23078PND5G` updated from the previous Play-installed v1 to v2 through Google Play. Final package evidence: versionCode 2, versionName 1.0, `installerPackageName=com.android.vending`, lastUpdateTime `2026-09-27 13:28:46`.
- After update, `com.anakinyoo.qrscanners/.MainActivity` launched successfully on the Xiaomi.
- Play review displayed advisory warnings for deobfuscation mapping and native debug symbols. Release minification is disabled, so no mapping file is generated; no fabricated symbol artifact was uploaded.
