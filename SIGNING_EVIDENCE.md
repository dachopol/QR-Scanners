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

Release signing now has a dedicated developer-controlled upload key. First signed-AAB acceptance by Play Console remains **TO VERIFY**.

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

## Safe next step

1. Configure CI with encrypted signing secrets derived from this dedicated upload key.
2. Produce a signed AAB from the same verified release commit.
3. Upload that first signed AAB to Play Console.
4. Record the upload-certificate fingerprint shown by Play Console and verify it matches the fingerprint above.
5. Never commit keystore files, passwords, base64 keystore material, or secret values to the repository.
