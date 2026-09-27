# QR Scanners Test Plan & Verification Matrix

## PASS — source / automated
- CameraX + ML Kit scanner implementation and debounce logic.
- Android Photo Picker gallery scan without broad media permission.
- QR generation for Text, URL, Wi-Fi, Contact, Email, Phone, SMS, and Geo.
- Wi-Fi QR escaping/parsing round-trip is unit-tested for special characters.
- Geo parser validates latitude -90..90 and longitude -180..180; invalid input is not converted to 0,0.
- Map action tries Google Maps, then another map handler, then browser fallback.
- Location QR can request optional foreground coarse/fine location and auto-fill current latitude/longitude.
- No background-location permission is declared.
- History/favorites/local persistence, theme, Thai/English/System language and responsive handset/tablet layout are implemented.
- CI rejects hardcoded UI labels in checked Compose/Toast paths and rejects EN/TH string-key mismatch.
- Current-main CI must pass unit tests, lint, debug/release builds, AAB build and 16 KB APK alignment.

## PASS — physical Android device
Verified on realme RMX3241, Android 13 / API 33. See `REAL_DEVICE_EVIDENCE.md`.
- Current build cold-launches without a fatal crash.
- RC debug installs side-by-side as `com.anakinyoo.qrscanners.rc` / `1.0-rc`, preserving the existing package/data.
- Camera permission flow reaches the Android system permission dialog and scanner screen.
- CameraX opens a real camera with Preview and ImageAnalysis active.
- Torch hardware path works; camera metadata reports TORCH/FIRED.
- Zoom hardware path works; camera zoom ratio changes after the in-app zoom control.
- Front/rear switching closes camera id 0 and opens camera id 1.
- Thai dark-theme Settings UI renders on a 1080x2400 device without observed overflow in the captured screen.
- Current-location provider populates latitude/longitude fields; exact user coordinates are intentionally not retained in evidence.
- Photo Picker selected a QR image and the exact payload was persisted with `isGenerated=false`.
- Geo QR decode opened Google Maps with a non-user test coordinate fixture.
- WEP Connect action fell back to Wi-Fi Settings.
- WPA2 Connect action issued a real `WifiNetworkSpecifier` request verified in logcat without touching the active network.

## TO VERIFY — physical Android device
- Decode a known QR/barcode through the live camera and verify the exact payload/result action.
- Complete a successful connection against an authorized real open/WPA2/WPA3 test network.

## PASS — release / Play Console
- Dedicated upload-keystore secrets are configured in CI and the recorded release APK/AAB passed signature verification.
- Play App Signing is active and the first signed AAB was accepted; upload-certificate fingerprints match the dedicated QR Scanners upload key.
- Privacy policy is published at the recorded public HTTPS URL and linked from the app / Play Console workflow.
- Six real-device release screenshots and required store assets are recorded in the repository.
- Data Safety, current no-ads declaration, target audience, content rating, and Internal testing rollout are recorded as completed for the current Internal build.

## TO VERIFY — remaining release / Production
- Complete Play Store installation through the Internal-test flow on an authenticated tester device and verify the installer source.
- Complete the developer account requirement of at least 12 opted-in closed-test testers for at least 14 days before Production Access.
- If monetization is enabled for Production, integrate real ads + one-time Ad-Free billing with real account/product configuration, test entitlement restore/ad suppression, and re-verify Data Safety / Ads / Billing declarations.

No device-specific or account-specific item may be marked PASS without real evidence.
