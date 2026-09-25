# QR Scanners — Release Gate

Source of Truth: `main`. GitHub Actions status for the current HEAD is authoritative; this file intentionally does not pin a commit SHA.

| Gate | Status | Evidence / next action |
|---|---|---|
| Package identity | PASS | `com.anakinyoo.qrscanners` |
| Google Play target API | PASS | `targetSdk = 36`; reverify current Play policy immediately before submission |
| Photo Picker | PASS | `ActivityResultContracts.PickVisualMedia` |
| Old/unused app SDK cleanup | PASS | Firebase/Retrofit/OkHttp/Moshi/KSP/old namespaces removed from active app config |
| ML Kit Data Safety disclosure | PASS | Privacy/Data Safety docs include ML Kit diagnostics/usage telemetry |
| Backup/privacy alignment | PASS | `android:allowBackup="false"` |
| Debug + unit + lint build | PASS | Current-main GitHub Actions gate |
| Release AAB build | PASS | Release AAB produced by CI |
| 16 KB APK alignment | PASS | `zipalign -c -P 16` in CI |
| Current-location source flow | PASS | Optional foreground coarse/fine location + automatic Lat/Lng fill; no background location |
| Release signing pipeline | PASS | CI can decode an upload keystore only when all signing secrets exist and verifies signed APK/AAB |
| Release signing evidence | TO VERIFY | Requires real `KEYSTORE_BASE64`, `STORE_PASSWORD`, `KEY_PASSWORD`, and `KEY_ALIAS` secrets |
| Store listing copy | PASS | See `STORE_LISTING.md` |
| Play Store app icon | GAP | Replace `store-assets/play_store_icon_512.jpg` with verified 512x512 32-bit PNG before Play upload |
| In-app privacy policy | PASS | Full EN/TH policy text is accessible from Settings |
| Privacy policy URL | TO VERIFY | Publish current policy at an active HTTPS URL and link it in Play; add the final URL to the release app if required by review |
| App cold launch | PASS | Realme RMX3241 / Android 13: process remains alive, MainActivity is top-resumed, no fatal crash |
| CameraX preview / analysis | PASS | Real device CameraX opened camera id 0 with Preview + ImageAnalysis active |
| Torch / flash | PASS | Real device camera metadata reported `flash.mode=TORCH` and `flash.state=FIRED` |
| Zoom | PASS | Real device `android.control.zoomRatio` changed from 1.0000 to 1.12676060 |
| Front/rear camera switching | PASS | CameraX closed camera id 0 and opened camera id 1 on switch |
| Gallery scan result | TO VERIFY | Photo Picker opened, but end-to-end selection test was interrupted by another foreground app |
| Current location / Map handoff | TO VERIFY | Physical device test was interrupted before permission/result verification |
| Wi-Fi connect action | TO VERIFY | Requires physical device/network verification |
| Store screenshots / content rating / ads status | TO VERIFY | Play Console + real release UI |
| Testing-track requirement | TO VERIFY | Confirm actual developer-account eligibility/status |

See `REAL_DEVICE_EVIDENCE.md` for the real-device test record.

Production is not ready until every applicable GAP/TO VERIFY item has real evidence.

Current policy references:
- Target API: https://support.google.com/googleplay/android-developer/answer/11926878
- Preview assets/icon: https://support.google.com/googleplay/android-developer/answer/9866151
- ML Kit disclosure: https://developers.google.com/ml-kit/android-data-disclosure
