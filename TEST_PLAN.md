# QR Scanners Test Plan & Verification Matrix

## 1. Feature Coverage
- [x] Camera Scanning: CameraX with ML Kit analyzer and debounce logic.
- [x] Viewfinder & Laser: Animated scan guide and responsive bounding box.
- [x] Flash Toggle: Real-time torch on/off.
- [x] Zoom Control: 1x to 5x pinch and slider zoom.
- [x] Gallery Image Scan: Android Photo Picker integration without broad media permissions.
- [x] Result Handling:
  - URLs: Browser launch, web search, copy, share.
  - Wi-Fi: Parsing SSID, password, security type, auto-connect intent.
  - Contact: vCard parsing, add to contacts.
  - Phone / SMS / Email: Appropriate intent dispatch.
  - Geo: Map viewing coordinates.
- [x] QR Generator:
  - Text, URL, Wi-Fi, Contact, Email, Phone, SMS, Geo.
  - Multi-color palette selection.
  - Bitmap generation via ZXing.
  - Sharing via Android FileProvider.
- [x] History & Favorites:
  - Persistence via JSON store.
  - Search, filtering, item deletion, wipe all, and CSV export.
- [x] Preferences & Settings:
  - Dark / Light / System theme.
  - Thai / English / System locale.
  - Vibrate, sound, auto-copy, auto-open toggles.
- [x] Responsive / Adaptive UI:
  - Handset portrait.
  - Tablet / Foldable NavigationRail wide view.

## 2. Automated Tests
- `ScanActionResolverTest`: Resolves URL, Wi-Fi, Contact, Phone, SMS, Geo, Text.
- `QrPayloadBuilderTest`: Generates valid QR payloads for all types.
