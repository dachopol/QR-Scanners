# QR Scanners — Real Device Evidence

Test date: 2026-09-25 to 2026-09-26  
Primary functional source commit tested: `6d4e5ab910b8d44d879abf770d4eaf251fb352d3`  
Continuation RC commit: `9e87df4eeb69edf0aa0275d52a5f8e32e2dd9362`  
Device: realme RMX3241  
Android: 13 / API 33  
Physical display: 1080x2400, density 480

## PASS

- **Install / cold launch:** debug APK installed on the physical device. After launch, `com.anakinyoo.qrscanners/.MainActivity` remained the top-resumed activity and the app process remained alive. A fresh logcat check contained no fatal exception.
- **RC side-by-side install:** current-main debug is isolated as `com.anakinyoo.qrscanners.rc` / version `1.0-rc` and installed successfully without uninstalling or overwriting the existing `com.anakinyoo.qrscanners` app. This avoids data loss when debug signing keys differ.
- **Splash crash root cause fixed:** the earlier real-device crash caused by loading the monochrome launcher resource through `painterResource` was reproduced, traced to a `BitmapDrawable` cast, changed to vector-resource loading, rebuilt, reinstalled, and verified not to recur.
- **Thai / dark UI:** Settings rendered in Thai on the 1080x2400 device; the captured screen showed the settings cards and bottom navigation without observed overflow.
- **Camera permission flow:** the app reached the Android system camera permission dialog and, after user approval, displayed the scanner controls and camera preview container.
- **CameraX:** logcat showed camera id 0 opening successfully with both Preview and ImageAnalysis use cases active/attached.
- **Torch:** after the in-app flash control was used, camera service metadata reported `android.flash.mode [TORCH]` and `android.flash.state [FIRED]`.
- **Zoom:** camera metadata changed from `android.control.zoomRatio [1.00000000]` to `[1.12676060]` after the in-app zoom-in control.
- **Front/rear switch:** CameraX logs showed camera id 0 detach/close, followed by camera id 1 Preview + ImageAnalysis attach and transition to OPEN.
- **Current-location auto-fill:** selecting the Geo generator on the physical device invoked the real location provider and populated both latitude and longitude fields. Exact coordinates are intentionally not recorded in this evidence file.

## TO VERIFY

- Decode a known QR/barcode through the **live camera** and compare the exact payload.
- Complete **Photo Picker** image selection and verify the exact decoded payload. Repeated continuation attempts were interrupted when another app became foreground, so this is not counted as PASS or FAIL.
- **Map handoff** from a decoded Geo QR on the physical device.
- **Wi-Fi connection action** against real open/WPA2/WPA3/WEP networks.
- **Release signing** with the actual upload keystore.
- **Play Console** listing/screenshots/content rating/ads declaration/testing-track requirements.

## Notes

- Functional QR/location/camera code did not change between the primary real-device commit and the continuation RC; later changes in that range are documentation, launcher/store icon assets, and the debug `applicationIdSuffix`.
- The original package was preserved after an `INSTALL_FAILED_UPDATE_INCOMPATIBLE` signature mismatch. No uninstall was performed, so existing app-private data/history was not deliberately erased.
- Device-specific items remain TO VERIFY unless this file records direct evidence.
