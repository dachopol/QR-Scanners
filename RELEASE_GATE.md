# QR Scanners — Release Gate

Source of Truth: `main`. GitHub Actions status for the current HEAD is authoritative; this file intentionally does not pin a commit SHA.

| Gate | Status | Evidence / next action |
|---|---|---|
| Package identity | PASS | Release application ID remains `com.anakinyoo.qrscanners`; debug RC uses `.rc` suffix only |
| Google Play target API | PASS | `targetSdk = 36`; reverify current Play policy immediately before submission |
| Photo Picker source flow | PASS | `ActivityResultContracts.PickVisualMedia`; no broad media permission |
| Photo Picker end-to-end result | PASS | Real device selected an image and persisted exact decoded payload with `isGenerated=false` |
| Old/unused app SDK cleanup | PASS | Firebase/Retrofit/OkHttp/Moshi/KSP/old namespaces removed from active app config |
| ML Kit Data Safety disclosure | PASS | Privacy/Data Safety docs account for documented ML Kit diagnostics/usage telemetry |
| Backup/privacy alignment | PASS | `android:allowBackup="false"` |
| Debug + unit + lint build | PASS | Current-main GitHub Actions gate |
| Release AAB build | PASS | Release AAB produced by CI |
| 16 KB APK alignment | PASS | `zipalign -c -P 16` in CI |
| Current-location source flow | PASS | Foreground coarse/fine location only; automatic Lat/Lng fill verified on real device |
| Map handoff | PASS | Decoded Geo test fixture opened Google Maps on the physical device |
| CameraX preview / analysis | PASS | Real device opened camera id 0 with Preview + ImageAnalysis |
| Torch / flash | PASS | Real device metadata reported TORCH/FIRED |
| Zoom | PASS | Real device camera zoom ratio changed after in-app control |
| Front/rear camera switching | PASS | Real device closed camera id 0 and opened id 1 |
| Live-camera exact payload decode | TO VERIFY | Requires presenting a known QR/barcode to the physical camera and comparing the exact result |
| WEP fallback | PASS | Connect action opened Wi-Fi Settings; no false success claim |
| WPA2 NetworkRequest path | PASS | Logcat recorded real `WifiNetworkSpecifier` request from the RC package |
| Successful Wi-Fi association | TO VERIFY | Requires an authorized real open/WPA2/WPA3 test network |
| Release signing pipeline | PASS | CI supports upload-keystore secrets and verifies signed APK/AAB when configured |
| Release signing evidence | TO VERIFY | Requires real `KEYSTORE_BASE64`, `STORE_PASSWORD`, `KEY_PASSWORD`, and `KEY_ALIAS` secrets |
| Store listing copy | PASS | See `STORE_LISTING.md` |
| Play Store app icon | PASS | `store-assets/play_store_icon_512.png` is the current verified 512x512 RGBA asset |
| In-app privacy policy | PASS | EN/TH policy text is accessible from Settings |
| Privacy policy URL | TO VERIFY | Publish current policy at an active HTTPS URL and use it in the Play listing/final release |
| Store screenshots / content rating / ads status | TO VERIFY | Play Console + real release UI |
| Testing-track requirement | TO VERIFY | Confirm actual developer-account eligibility/status |

See `REAL_DEVICE_EVIDENCE.md` for physical-device evidence.

Production is not ready until every applicable TO VERIFY item has real evidence.

Current policy references:
- Target API: https://support.google.com/googleplay/android-developer/answer/11926878
- Preview assets/icon: https://support.google.com/googleplay/android-developer/answer/9866151
- ML Kit disclosure: https://developers.google.com/ml-kit/android-data-disclosure
