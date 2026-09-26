# QR Scanners — Play Console Checklist

## PASS before account-specific work
- Package: `com.anakinyoo.qrscanners`
- targetSdk 36
- Android App Bundle builds in CI
- 16 KB APK alignment gate
- No broad media permission; gallery uses Photo Picker
- No background location permission
- Backup disabled
- ML Kit disclosure documented
- Store listing draft prepared in EN/TH
- Current source contains no advertising SDK; Play Ads declaration should remain "No" unless the release build changes
- Current source has no login/account gate; app-access review does not need test credentials unless the release build changes
- Optional signed-release pipeline prepared without committing secrets
- Play Store icon is the verified 512x512 PNG asset
- Feature graphic prepared as `store-assets/feature_graphic_1024x500.jpg` (1024x500 JPEG, no alpha)
- Real-device gate passed for cold launch, launcher icon, camera preview, torch, zoom, camera switching, gallery decode, location auto-fill, map handoff, WEP fallback, WPA2 request path, history and TH/EN switching
- Six Thai release screenshots captured from release UI 1.0 and stored in `store-assets/screenshots/th/`
- Source-backed form-entry draft prepared in `PLAY_CONSOLE_ANSWERS.md`
- Privacy policy is published at the public HTTPS raw GitHub URL, returned HTTP 200 without authentication, and is linked from the app

## GAP / TO VERIFY before upload
- Enter the verified privacy-policy URL in the Play Console store listing/app-content fields.
- Configure real upload-key secrets and obtain a CI run with signature verification PASS.
- Verify a known QR/barcode exact payload through the live camera on a physical device.
- Complete a successful association against an authorized real open/WPA2/WPA3 test network.
- Review final Data Safety answers against the final dependency tree.
- Complete Content rating, Target audience, App access, Ads declaration, and other Play Console forms.
- Confirm the testing-track/closed-testing requirement shown for the actual developer account.
- Upload only the signed AAB from the same verified release commit.

## Current official references
- Target API: https://support.google.com/googleplay/android-developer/answer/11926878
- Store listing limits: https://support.google.com/googleplay/android-developer/answer/9859152
- Preview assets/icon: https://support.google.com/googleplay/android-developer/answer/9866151
- ML Kit data disclosure: https://developers.google.com/ml-kit/android-data-disclosure
