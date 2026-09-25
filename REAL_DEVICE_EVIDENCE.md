# QR Scanners — Real Device Evidence

Test date: 2026-09-25  
Source commit tested: `6d4e5ab910b8d44d879abf770d4eaf251fb352d3`  
Device: realme RMX3241  
Android: 13 / API 33  
Physical display: 1080x2400, density 480

## PASS

- **Install / cold launch:** debug APK from the current-main CI artifact installed on the physical device. After launch, `com.anakinyoo.qrscanners/.MainActivity` remained the top-resumed activity and the app process remained alive. A fresh logcat check contained no fatal exception.
- **Splash crash root cause fixed:** the earlier real-device crash caused by loading the monochrome launcher resource through `painterResource` was reproduced, traced to a `BitmapDrawable` cast, changed to vector-resource loading, rebuilt, reinstalled, and verified not to recur.
- **Thai / dark UI:** Settings rendered in Thai on the 1080x2400 device; the captured screen showed the settings cards and bottom navigation without observed overflow.
- **Camera permission flow:** the app reached the Android system camera permission dialog and, after user approval, displayed the scanner controls and camera preview container.
- **CameraX:** logcat showed camera id 0 opening successfully with both Preview and ImageAnalysis use cases active/attached.
- **Torch:** after the in-app flash control was used, camera service metadata reported `android.flash.mode [TORCH]` and `android.flash.state [FIRED]`.
- **Zoom:** camera metadata changed from `android.control.zoomRatio [1.00000000]` to `[1.12676060]` after the in-app zoom-in control.
- **Front/rear switch:** CameraX logs showed camera id 0 detach/close, followed by camera id 1 Preview + ImageAnalysis attach and transition to OPEN.

## TO VERIFY

- Decode a known QR/barcode through the **live camera** and compare the exact payload.
- Complete **Photo Picker** image selection and verify the exact decoded payload. The previous picker attempt was interrupted when another app became foreground, so it is not counted as PASS or FAIL.
- **Current location / map handoff** on the physical device. The attempted navigation was interrupted before permission/result verification; no precise location was recorded in this evidence file.
- **Wi-Fi connection action** against real open/WPA2/WPA3/WEP networks.
- **Release signing** with the actual upload keystore.
- **Play Console** listing/screenshots/content rating/ads declaration/testing-track requirements.

Device-specific items remain TO VERIFY unless this file records direct evidence.
