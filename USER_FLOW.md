# QR Scanners — User Flow

Status: current flow documented from the implemented Compose source.

## App entry

Launch
→ Splash
→ Main app

Main navigation:
- Scan
- Create
- History
- Settings

Compact devices use bottom navigation. Wide devices use a navigation rail.

## Scan flow

Scan tab
→ If camera permission granted: CameraX preview + analysis
→ If camera permission denied/not granted: permission UI remains available and Photo Picker can still be used
→ QR/barcode detected
→ Live analyzer pauses
→ Result sheet opens
→ User may perform action / copy / share / favorite
→ Dismiss result
→ Live analyzer resumes

### Photo flow

Scan tab
→ Choose image
→ Android Photo Picker
→ ML Kit analyzes only selected image
→ If code found: Result sheet
→ If none/error: localized error state

## Result action flow

Decoded type
→ URL → Browser
→ Wi-Fi → Parse credentials
   → Android 13+ and permission missing → request Nearby Wi-Fi permission
   → permission granted → WifiNetworkSpecifier request
   → unavailable/unsupported/error → safe Wi-Fi Settings fallback
   → WEP → Wi-Fi Settings fallback
→ Contact → Add Contact
→ Phone → Dialer
→ SMS → Messaging
→ Email → Email client
→ Geo → Map/browser
→ Text → copy/share/search actions as available

No external handoff is reported as successful unless the app/system actually performs that flow.

## Create flow

Create tab
→ Select QR type
→ Enter relevant fields
→ For Geo, optionally request foreground location to fill coordinates
→ Generate QR
→ Preview
→ Save to local history
→ Share QR if requested

## History flow

History
→ Search/filter
→ Select record → Result sheet
→ Favorite / action / share
→ Delete individual item
or
→ Clear all with confirmation
or
→ Export CSV with sensitive Wi-Fi credentials redacted

## Settings flow

Settings
→ Theme: System / Light / Dark
→ Language: System / English / Thai
→ Scan behavior: vibration / sound / auto-copy / auto-open URL / default camera
→ Privacy policy / app version

## Release/QA flow

Source change
→ GitHub Actions Android Release Gate
→ unit + lint + Debug/Release + AAB
→ 16 KB alignment
→ signing verification
→ physical-device targeted regression
→ Play test track
→ required closed testing
→ Production Access only after all applicable gates have evidence
