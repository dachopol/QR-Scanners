# QR Scanners — Real Device Evidence

Test window: 2026-09-25 to 2026-09-26  
Primary functional source commit: `6d4e5ab910b8d44d879abf770d4eaf251fb352d3`  
RC isolation commit: `9e87df4eeb69edf0aa0275d52a5f8e32e2dd9362`  
Device: realme RMX3241  
Android: 13 / API 33  
Physical display: 1080x2400, density 480

## PASS

- **Install / cold launch:** debug APK installed on the physical device. After launch, `com.anakinyoo.qrscanners/.MainActivity` remained top-resumed and the app process stayed alive without a fatal exception in the verification log.
- **RC side-by-side install:** current-main debug uses `com.anakinyoo.qrscanners.rc` / `1.0-rc` and installed successfully without uninstalling or overwriting the existing `com.anakinyoo.qrscanners` package. Existing app-private data/history was preserved.
- **Splash crash root cause fixed:** the earlier launcher-resource crash was reproduced, traced to a bitmap/vector mismatch, fixed, rebuilt and verified not to recur on the device.
- **Thai / dark UI:** Settings rendered in Thai on the 1080x2400 device without observed overflow in the captured screen.
- **Camera permission flow:** the app reached the Android system camera-permission dialog and, after approval, displayed the scanner controls and camera preview container.
- **CameraX:** camera id 0 opened with Preview + ImageAnalysis attached.
- **Torch:** camera metadata reported `android.flash.mode [TORCH]` and `android.flash.state [FIRED]`.
- **Zoom:** camera metadata changed from `android.control.zoomRatio [1.00000000]` to `[1.12676060]` after the in-app zoom control.
- **Front/rear switch:** CameraX detached/closed camera id 0 and opened camera id 1 with Preview + ImageAnalysis.
- **Current-location auto-fill:** selecting Geo on the physical device invoked the real location provider and populated latitude and longitude. Exact user coordinates are intentionally not retained in this evidence file.
- **Photo Picker end-to-end decode:** a generated test QR was captured, cropped, added to Pictures and selected through Android Photo Picker. The RC history then contained the exact payload `QRSCANNERS_TEST_20260926` with `isGenerated=false`, proving gallery selection + ML Kit decode + app result persistence.
- **Geo decode + Map handoff:** a non-user test fixture `geo:1.234567,2.345678` decoded as `GEO`; history stored the exact payload and Android foreground changed to `com.google.android.apps.maps/com.google.android.maps.MapsActivity`.
- **WEP fallback:** a WEP test QR decoded as Wi-Fi and the Connect action opened the device Wi-Fi Settings activity instead of claiming an automatic connection.
- **WPA2 request path:** a nonexistent test SSID was used so the active network would not be disturbed. Android logcat recorded a real `ConnectivityService requestNetwork` with `WifiNetworkSpecifier`, the exact test SSID and `RequestorPkg: com.anakinyoo.qrscanners.rc`; Android opened the network-request resolver/dialog.

## TO VERIFY

- Decode a known QR/barcode through the **live camera** and compare the exact payload/result action.
- Complete a **successful Wi-Fi association** against an authorized real open/WPA2/WPA3 test network. WEP fallback and WPA2 request creation are already verified; network success itself is not.
- **Release signing** with the actual upload keystore.
- **Privacy policy HTTPS URL** used by the final Play listing/release.
- **Play Console** listing, screenshots, content rating, ads declaration and testing-track/account requirements.

## Notes

- Test payloads/SSIDs in this file are synthetic fixtures and are not represented as real user data.
- Functional QR/location/camera code did not materially change across the documented real-device continuation; later commits in that span are evidence/docs, launcher/store assets and debug-package isolation.
- The original package was preserved after an `INSTALL_FAILED_UPDATE_INCOMPATIBLE` debug-signature mismatch. No uninstall was performed.
- Device/account-specific items remain TO VERIFY unless direct evidence is recorded here.
