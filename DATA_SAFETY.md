# Google Play Data Safety — release draft

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

## Play Console status
Do **not** declare “no data collected” without reviewing the final release dependency tree and the current Play Console definitions. Final Data Safety answers remain **TO VERIFY** immediately before release.
