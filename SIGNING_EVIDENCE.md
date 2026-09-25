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

Release signing therefore remains **TO VERIFY**.

## Safe next step

1. If `com.anakinyoo.qrscanners` already exists in Play Console, open **App integrity / App signing** and compare the expected upload certificate before using any local key.
2. If this package is genuinely new and has never been registered/uploaded, create a dedicated upload key only after explicit approval, store it outside the repository, and configure CI through encrypted secrets.
3. Never commit keystore files, passwords, base64 keystore material, or secret values to the repository.
