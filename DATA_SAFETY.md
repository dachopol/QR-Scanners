# Google Play Data Safety — verified Play Console declaration

**App:** QR Scanners  
**Package:** `com.anakinyoo.qrscanners`

## Current source facts
- QR/barcode image analysis uses `com.google.mlkit:barcode-scanning`.
- Scanned/generated payloads and history are stored in app-private local storage.
- Gallery scanning uses Android Photo Picker; no broad photo/media permission is requested.
- Camera is optional at runtime; gallery scan remains available without camera permission.
- Foreground coarse/fine location is optional and used to fill a Location QR with the device's current coordinates. No background-location permission is requested and current coordinates are not sent to a developer backend.
- App backup is disabled in the manifest.
- No developer backend, account system, ads SDK, billing SDK, Firebase AI, Firebase App Check, Retrofit, OkHttp, or custom analytics SDK is included in the current app module.

## ML Kit disclosure
Google documents that ML Kit Android SDKs collect device/app information, per-installation or device identifiers, performance metrics, API configuration, input/output size, feature version, event type, and error codes for diagnostics and usage analytics. Data is encrypted in transit. Google states the listed ML Kit data is not transferred to third parties.

Barcode auto-zoom is **not enabled** in this source, so the additional auto-zoom session/zoom telemetry described by Google is not expected from this app configuration.

Official reference:
https://developers.google.com/ml-kit/android-data-disclosure

## Play Console status — verified 2026-09-27
The Data Safety declaration was completed and saved in Google Play Console against the current internal-test release.

Final declared handling:
- **Collects required user data:** Yes
- **Shared with third parties:** No
- **Encrypted in transit:** Yes
- **Accounts:** The app does not allow users to create accounts and does not support sign-in with accounts created elsewhere.
- **Collected data types:** Diagnostics; Device or other IDs
- **Diagnostics:** collected, not shared, not processed ephemerally, required/automatic, purpose = Analytics
- **Device or other IDs:** collected, not shared, not processed ephemerally, required/automatic, purpose = Analytics
- Local-only scan payloads, Photo Picker images, camera frames, history, favorites, settings, and optional foreground location are not declared as collected because the current app does not transmit them off-device.
- App Content summary shows no remaining action-required declarations after saving Data Safety.

Re-verify this declaration before any production build that adds Ads SDK, Billing, analytics, backend, account, or other data-transmitting dependencies.
