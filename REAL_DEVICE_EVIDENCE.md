# QR Scanners — Real Device Evidence

Test window: 2026-09-25 to 2026-09-27  
Primary functional source commit: `6d4e5ab910b8d44d879abf770d4eaf251fb352d3`  
RC isolation commit: `9e87df4eeb69edf0aa0275d52a5f8e32e2dd9362`  
Primary functional device: realme RMX3241  
Android: 13 / API 33  
Physical display: 1080x2400, density 480  

Secondary Play-delivery device: Xiaomi 23078PND5G  
Android: 16  
Physical display: 1220x2712

## PASS

- **Install / cold launch:** debug APK installed on the physical device. After launch, `com.anakinyoo.qrscanners/.MainActivity` remained top-resumed and the app process stayed alive without a fatal exception in the verification log.
- **RC side-by-side install:** current-main debug uses `com.anakinyoo.qrscanners.rc` / `1.0-rc` and installed successfully without uninstalling or overwriting the existing `com.anakinyoo.qrscanners` package. Existing app-private data/history was preserved.
- **Splash crash root cause fixed:** the earlier launcher-resource crash was reproduced, traced to a bitmap/vector mismatch, fixed, rebuilt and verified not to recur on the device.
- **Launcher icon / OEM compatibility:** commit `776e80ff8695ea4d8dbed7b11ad1c1c1e05c134d` replaced the legacy anydpi bitmap XML fallback with density-specific PNG launcher assets and a safe adaptive foreground. The resulting RC passed CI, installed on the realme device, cold-launched without a fatal exception, and the Oplus launcher search rendered the branded QR Scanners icon instead of the default Android icon.
- **Thai / dark UI:** Settings rendered in Thai on the 1080x2400 device without observed overflow in the captured screen.
- **Camera permission flow:** the app reached the Android system camera-permission dialog and, after approval, displayed the scanner controls and camera preview container.
- **CameraX:** camera id 0 opened with Preview + ImageAnalysis attached.
- **Live-camera exact payload decode:** on 2026-09-27 the realme RC scanner decoded known fixture `QRSCANNERS_LIVE_20260927_V2` through the physical camera. The result UI showed the exact payload. Debug-package storage then persisted the same record in `files/history_records.json` with `content=QRSCANNERS_LIVE_20260927_V2`, `qrType=TEXT`, `barcodeFormat=QR Code`, and `isGenerated=false`.
- **Torch:** camera metadata reported `android.flash.mode [TORCH]` and `android.flash.state [FIRED]`.
- **Zoom:** camera metadata changed from `android.control.zoomRatio [1.00000000]` to `[1.12676060]` after the in-app zoom control.
- **Front/rear switch:** CameraX detached/closed camera id 0 and opened camera id 1 with Preview + ImageAnalysis.
- **Current-location auto-fill:** selecting Geo on the physical device invoked the real location provider and populated latitude and longitude. Exact user coordinates are intentionally not retained in this evidence file.
- **Photo Picker end-to-end decode:** a generated test QR was captured, cropped, added to Pictures and selected through Android Photo Picker. The RC history then contained the exact payload `QRSCANNERS_TEST_20260926` with `isGenerated=false`, proving gallery selection + ML Kit decode + app result persistence.
- **Geo decode + Map handoff:** a non-user test fixture `geo:1.234567,2.345678` decoded as `GEO`; history stored the exact payload and Android foreground changed to `com.google.android.apps.maps/com.google.android.maps.MapsActivity`.
- **WEP fallback:** a WEP test QR decoded as Wi-Fi and the Connect action opened the device Wi-Fi Settings activity instead of claiming an automatic connection.
- **WPA2 request path:** a nonexistent test SSID was used so the active network would not be disturbed. Android logcat recorded a real `ConnectivityService requestNetwork` with `WifiNetworkSpecifier`, the exact test SSID and `RequestorPkg: com.anakinyoo.qrscanners.rc`; Android opened the network-request resolver/dialog.
- **Release signing:** dedicated QR Scanners upload key was used by GitHub Actions; signed APK/AAB verification passed and Play Console recorded matching SHA-1/SHA-256 upload-certificate fingerprints.
- **Internal testing rollout:** Historical first rollout was version 1.0 / versionCode 1. On 2026-09-27, current-main source commit `c2ed27f85a1109d12bb04dd06443344f7ca88726` / versionCode 2 passed signed CI, was accepted by Play Console as `2 (1.0)` with min API 24+ / target SDK 36, and was published to Internal testing.
- **Internal tester selection:** the selected `QR Scanners Internal` list contains one verified tester account used for this workflow.
- **Internal opt-in link:** Play Console generated the internal-test opt-in URL and it was opened on the connected realme device.
- **Play Store install/update on clean tester device:** Xiaomi `23078PND5G` did not have `com.anakinyoo.qrscanners` before the first test. Google Play installed version 1.0 / versionCode 1 with `installerPackageName=com.android.vending`. After current-main versionCode 2 was published, the Play client showed Update; the update downloaded through Google Play / Play Protect and completed. Final `dumpsys package` reported versionName 1.0, versionCode 2, `installerPackageName=com.android.vending`, lastUpdateTime `2026-09-27 13:28:46`. Launching resumed `com.anakinyoo.qrscanners/.MainActivity` and rendered the v2 runtime UI. The existing realme sideload remained unchanged.
- **Current-main v2 runtime:** after the Play update, `com.anakinyoo.qrscanners/.MainActivity` was top-resumed on the Xiaomi. The app rendered normally; camera access remained an explicit runtime permission on that device.
- **Play Console declarations:** Data Safety, Ads declaration, target audience, and content rating were completed and recorded in the release documentation on 2026-09-27.

## TO VERIFY

- Complete a **successful Wi-Fi association** against an authorized real open/WPA2/WPA3 test network. WEP fallback and WPA2 request creation are already verified; network success itself is not.
- **Closed testing for Production Access:** this account's Console requires at least 12 opted-in testers for at least 14 days; that requirement has not yet been completed.

## Notes

- Test payloads/SSIDs in this file are synthetic fixtures and are not represented as real user data.
- Functional QR/location/camera code did not materially change across the documented real-device continuation; later commits in that span are evidence/docs, launcher/store assets and debug-package isolation.
- An earlier debug-signature mismatch (`INSTALL_FAILED_UPDATE_INCOMPATIBLE`) blocked replacing the old test package. After explicit user approval, that obsolete test package was uninstalled; later RC validation uses the isolated `.rc` debug package.
- The Play-delivered Xiaomi base APK reports `minSdkVersion=32` while `main` and all recorded `app/build.gradle.kts` history specify `minSdk=24`. After the current Play update, package/version/targetSdk are `com.anakinyoo.qrscanners`, 1.0(2), target 36. Play Console accepted the source bundle itself as min API 24+. The installed-device base APK value is recorded as device-targeted delivery evidence and is not treated as a source-config change.
- The Google Play tester-client page still shows `com.anakinyoo.qrscanners (unreviewed)` with a generic Android placeholder after v2 delivery. The Play Console listing icon asset is separately verified and saved; tester-client icon propagation remains TO VERIFY after review.
- Device/account-specific items remain TO VERIFY unless direct evidence is recorded here.
