# QR Scanners — Release Signing

Do not commit a keystore, passwords, aliases, or exported secret files.

## GitHub Actions secrets
Configure all four secrets together:
- `KEYSTORE_BASE64`: base64 of the real upload keystore file.
- `STORE_PASSWORD`: keystore password.
- `KEY_PASSWORD`: upload key password.
- `KEY_ALIAS`: exact alias inside the keystore.

If any value is missing, CI intentionally produces unsigned release artifacts and reports signing as TO VERIFY.

## Create KEYSTORE_BASE64 locally
Linux/macOS:
```bash
base64 < upload-keystore.jks | tr -d '\n'
```

PowerShell:
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("upload-keystore.jks"))
```

Paste the resulting string into the GitHub Actions secret. Never paste it into source code, issues, logs, or chat messages.

## CI verification
When all secrets exist, the workflow:
1. Decodes the keystore into the GitHub runner temporary directory.
2. Passes the temporary path and secret credentials only to the release build.
3. Builds release APK/AAB.
4. Verifies the APK with `apksigner`.
5. Verifies the AAB with `jarsigner`.

A successful unsigned build is not release-signing evidence. Only a run that reports signing verification PASS is acceptable for Play upload.
