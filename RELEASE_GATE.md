# QR Scanners — Release Gate

Source of Truth: `main`. GitHub Actions status for the current HEAD is authoritative; this file intentionally does not pin a commit SHA.

| Gate | Status | Evidence / next action |
|---|---|---|
| Package identity | PASS | Release application ID is `com.anakinyoo.qrscanners`; debug RC uses `.rc` suffix only |
| Google Play target API | PASS | `targetSdk = 36`; Google Play requires API 36+ for new apps and app updates from 31 Aug 2026 |
| Photo Picker source flow | PASS | `ActivityResultContracts.PickVisualMedia`; no broad media permission |
| Photo Picker end-to-end result | PASS | Real device selected an image and persisted the exact decoded payload with `isGenerated=false` |
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
| WPA2 NetworkRequest path | PASS | Logcat recorded a real `WifiNetworkSpecifier` request from the RC package |
| Successful Wi-Fi association | TO VERIFY | Requires an authorized real open/WPA2/WPA3 test network |
| Release signing pipeline | PASS | CI supports upload-keystore secrets and verifies signed APK/AAB when configured |
| Signing lineage | PASS | Existing QuickQR Business key was proven to sign a different package and is explicitly rejected for automatic reuse; see `SIGNING_EVIDENCE.md` |
| Release signing evidence | TO VERIFY | Need the Play-expected upload certificate or explicit approval to create a dedicated new upload key |
| Ads SDK / Ad-Free billing implementation | GAP | Intentionally absent during testing. Before Production: integrate ads + Google Play Billing, create one-time Ad-Free product, verify entitlement restore and ad suppression on real device |
| Store listing copy | PASS | See `STORE_LISTING.md` |
| Play Store app icon | PASS | `store-assets/play_store_icon_512.png` is the current 512x512 RGBA asset |
| Android launcher icon / OEM fallback | PASS | Density PNG legacy assets + adaptive foreground verified on realme/Oplus launcher; branded icon rendered after RC reinstall |
| Feature graphic | PASS | `store-assets/feature_graphic_1024x500.jpg` is a verified 1024x500 RGB JPEG with no alpha, matching current Google Play preview-asset requirements |
| In-app privacy policy | PASS | EN/TH policy text is accessible from Settings |
| Privacy policy URL | PASS | Public HTTPS raw GitHub URL returned HTTP 200 without authentication, names QR Scanners and AnakinYoo, provides a privacy inquiry mechanism, is linked from the app, and is not a PDF |
| Data Safety final answers | TO VERIFY | Recheck final release dependency tree and Play Console definitions immediately before submission |
| Store screenshots | PASS | Six Thai phone screenshots from release UI 1.0 on the verified realme device are stored in `store-assets/screenshots/th/`; each is 1080x2160 RGB PNG with system status/navigation bars removed |
| Ads declaration | TO VERIFY | Test build has no ads SDK. Production plan is ads + one-time Ad-Free purchase; declaration must switch to Yes only when the ad-enabled release source is integrated and verified |
| Content rating / target audience | TO VERIFY | Play Console |
| Personal-account closed-test requirement | TO VERIFY | If the personal developer account was created after 13 Nov 2023: at least 12 opted-in testers continuously for 14 days before production-access application |

See `REAL_DEVICE_EVIDENCE.md` and `SIGNING_EVIDENCE.md`.

Production is not ready until every applicable GAP/TO VERIFY item has real evidence.

Current policy references:
- Target API: https://support.google.com/googleplay/android-developer/answer/11926878
- Personal-account testing: https://support.google.com/googleplay/android-developer/answer/14151465
- User Data / Privacy Policy: https://support.google.com/googleplay/android-developer/answer/10144311
- Preview assets: https://support.google.com/googleplay/android-developer/answer/9866151
- ML Kit disclosure: https://developers.google.com/ml-kit/android-data-disclosure
