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

## GAP / TO VERIFY before upload
- Replace Store icon JPG with 512x512 32-bit PNG (alpha, <=1024 KB).
- Publish privacy policy to a stable active HTTPS URL.
- Link the privacy policy from the app and Play listing.
- Configure real upload-key secrets and obtain a CI run with signature verification PASS.
- Test camera, torch, zoom, camera switching, gallery scan, location, map handoff, Wi-Fi actions, share, history, favorites, TH/EN on a physical Android device.
- Capture screenshots from the validated release UI.
- Review final Data Safety answers against the final dependency tree.
- Complete Content rating, Target audience, App access, Ads declaration, and other Play Console forms.
- Confirm the testing-track/closed-testing requirement shown for the actual developer account.
- Upload only the signed AAB from the same verified release commit.

## Current official references
- Target API: https://support.google.com/googleplay/android-developer/answer/11926878
- Store listing limits: https://support.google.com/googleplay/android-developer/answer/9859152
- Preview assets/icon: https://support.google.com/googleplay/android-developer/answer/9866151
- ML Kit data disclosure: https://developers.google.com/ml-kit/android-data-disclosure
