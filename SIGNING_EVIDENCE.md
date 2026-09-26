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

No verified evidence currently shows that `com.anakinyoo.qrscanners` has already been uploaded to Google Play, that Play App Signing is enabled for this exact package, or which upload certificate Google Play expects.

Play Console evidence collected on 2026-09-26:
- The new Play Console app **QR Scanners** was created for package `com.anakinyoo.qrscanners`.
- **Play App Signing** shows **active**.
- Google Play is managing the app-signing key for this new app.
- The **upload-key certificate** section has no fingerprint yet; Play Console states that the certificate fingerprint will appear after the first App Bundle is uploaded.

Release signing therefore remains **TO VERIFY** only for the developer-controlled upload key and the first signed AAB upload.

## Safe next step

1. Create a dedicated upload key for `com.anakinyoo.qrscanners` only after explicit approval.
2. Store the keystore and passwords outside the repository.
3. Configure CI through encrypted secrets.
4. Sign the release AAB with that upload key and upload the first bundle to Play Console.
5. Confirm that Play Console shows the resulting upload-certificate fingerprint and preserve that fingerprint as release evidence.
6. Never commit keystore files, passwords, base64 keystore material, or secret values to the repository.
