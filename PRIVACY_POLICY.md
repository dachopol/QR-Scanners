# Privacy Policy for QR Scanners

**Effective date:** September 2026  
**Developer:** AnakinYoo

QR Scanners scans and creates QR codes and barcodes. Core scan content, generated content, settings, favorites, and history are handled in the app and stored in app-private storage unless you explicitly share or open content with another app.

## Camera and photos
- Camera access is used for real-time scanning. The app does not intentionally record or upload camera video.
- Gallery scanning uses Android Photo Picker. The selected image is processed for barcode detection without requesting broad photo-library access.

## Current location
- When you choose the Location QR type and tap the current-location action, the app can request foreground approximate/precise location permission and use the device's current coordinates to fill the latitude/longitude fields.
- Location is requested only while the app is in use. No background-location permission is requested.
- The app does not upload current coordinates to a developer backend. Coordinates are stored in local history only if you generate a location QR code.

## Local history and settings
- Scan and creation history, favorites, and app settings are stored in app-private storage.
- You can delete individual history records or clear all history from the app.
- Local app data remains until you delete it, clear the app's storage, or uninstall the app, subject to Android platform behavior.
- Android application backup is disabled for this app.

## Sharing and external apps
When you choose to share, open, dial, email, message, add a contact, open a map, or connect through system Wi-Fi flows, the selected content is handed to the relevant Android system component or external app. Data handled after that handoff is governed by that app or service's own privacy practices.

## ML Kit diagnostics and analytics
Barcode recognition uses Google ML Kit. Google states that ML Kit Android SDKs collect certain device/app information, identifiers, performance metrics, API configuration, feature input/output size, feature version, event types, and error codes for diagnostics and usage analytics. Google states this data is encrypted in transit and the listed ML Kit data is not transferred to third parties.

The current app does not enable ML Kit barcode auto-zoom.

ML Kit disclosure:
https://developers.google.com/ml-kit/android-data-disclosure

## Developer services
The current source does not include a developer-operated backend, advertising SDK, billing SDK, Firebase AI, Firebase App Check, or custom analytics SDK. QR Scanners does not provide user accounts.

## Privacy inquiries
For privacy questions or requests, use the developer contact mechanism displayed on the Google Play listing for QR Scanners. The published privacy-policy web page must use the same developer identity or app name shown on Google Play.

## Policy availability
Before release, this policy must be published at an active, publicly accessible, non-geofenced HTTPS URL that is not a PDF and is not editable by visitors. The URL must be entered in Play Console and made accessible from within the released app.

This policy must be reviewed again if dependencies or app behavior change before release.
