# QR Scanners Test Plan & Verification Matrix

## PASS — source / automated
- CameraX + ML Kit scanner implementation and debounce logic.
- Android Photo Picker gallery scan without broad media permission.
- QR generation for Text, URL, Wi-Fi, Contact, Email, Phone, SMS, and Geo.
- Geo parser validates latitude -90..90 and longitude -180..180; invalid input is not converted to 0,0.
- Map action tries Google Maps, then another map handler, then browser fallback.
- Location QR can request optional foreground coarse/fine location and auto-fill current latitude/longitude.
- No background-location permission is declared.
- History/favorites/local persistence, theme, Thai/English/System language, responsive handset/tablet layout are implemented.
- GitHub Actions run `35971510323` passed unit tests, lint, debug/release builds, AAB build, and 16 KB APK alignment.

## TO VERIFY — physical Android device
- Real camera scanning on supported devices.
- Torch/flash hardware behavior.
- Zoom and front/rear camera switching.
- Current-location permission prompt, automatic coordinate fill, GPS/network provider behavior, and map handoff with real coordinates.
- Wi-Fi connection request behavior on supported Android versions.

## TO VERIFY — release / Play Console
- Real upload-key signing and signed release AAB.
- Final dependency tree and Data Safety answers.
- Store listing, screenshots, content rating, target audience, ads declaration.
- Closed-test requirement for the actual developer account.

No device-specific item may be marked PASS without real evidence.
