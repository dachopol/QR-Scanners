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
- History/favorites/local persistence, theme, Thai/English/System language, responsive handset/tablet layout are implemented.
- CI rejects hardcoded UI labels in Compose/Toast paths and rejects EN/TH string-key mismatch.
- GitHub Actions for current main must pass unit tests, lint, debug/release builds, AAB build, and 16 KB APK alignment.

## TO VERIFY — physical Android device
- Real camera scanning on supported devices.
- Torch/flash hardware behavior.
- Zoom and front/rear camera switching.
- Current-location permission prompt, automatic coordinate fill, GPS/network provider behavior, and map handoff with real coordinates.
- Wi-Fi connection request behavior on supported Android versions, including open/WPA2/WPA3 and WEP fallback.

## TO VERIFY — release / Play Console
- Configure the real upload keystore secrets and verify a signed release APK/AAB in CI.
- Replace the repository Store icon JPG with a verified 512x512 32-bit PNG asset before Play upload.
- Host the privacy policy at an active HTTPS URL and link it from the Play listing and app.
- Capture real-device screenshots that accurately show the released UI; prepare localized screenshots when screenshots contain text.
- Recheck the final dependency tree and Data Safety answers.
- Complete store listing, content rating, target audience, ads declaration, and testing-track requirements for the actual developer account.

No device-specific or account-specific item may be marked PASS without real evidence.
