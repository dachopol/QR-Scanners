# QR Scanners

A fast, lightweight, and privacy-focused QR and barcode scanner, creator, and history manager built with Kotlin and Jetpack Compose.

## Key Features
- **Real-Time Camera Scanner**: CameraX + ML Kit for instant scanning with viewfinder and animated laser indicator.
- **Gallery Image Scanner**: Scan QR codes and barcodes directly from photos without uploading them anywhere.
- **Camera Controls**: Instant flash toggle, linear zoom slider (1x - 5x) with zoom buttons, front/rear camera flipping.
- **Smart Result Actions**:
  - URLs: One-tap browser launch, web search, copying, and sharing.
  - Wi-Fi: One-tap connect, copy network password.
  - Contacts (vCard/MeCard): Save directly to contacts, phone dialing, email drafting.
  - Phone & SMS: Direct dialing and SMS composition.
  - Geo Locations: Open coordinates in Google Maps or navigation apps.
- **Custom QR Generator**:
  - Generate QR codes for Text, URLs, Wi-Fi, Contacts, Email, Phone, SMS, and Geo.
  - Color palettes (Classic, Ocean Blue, Deep Purple, Emerald Green).
  - Share generated QR code images via Android Sharesheet.
- **Persistent History**:
  - Local JSON storage for scanned and generated items.
  - Filter by All, Scanned, Created, and Favorites.
  - Search bar, item deletion, favorite toggling, and CSV export.
- **Settings & Preferences**:
  - Theme mode: System Default, Light, Dark.
  - Language: System Default, English, Thai (ภาษาไทย).
  - Vibrate on scan, beep sound on scan, auto-copy to clipboard, auto-open URLs.
  - Default camera selection.
- **Privacy-First**: 100% on-device processing. No data is collected or transmitted off the device.
