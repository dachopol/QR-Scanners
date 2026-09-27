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
| Debug + unit + lint build | PASS | GitHub Actions Android Release Gate passed on the current app source; observed run `36294091474` completed successfully |
| Release AAB build | PASS | Release AAB produced successfully by the current-source CI gate; run `36294091474` passed |
| 16 KB APK alignment | PASS | `zipalign -c -P 16` passed in CI run `36294091474` |
| Current-location source flow | PASS | Foreground coarse/fine location only; automatic Lat/Lng fill verified on real device |
| Map handoff | PASS | Decoded Geo test fixture opened Google Maps on the physical device |
| CameraX preview / analysis | PASS | Real device opened camera id 0 with Preview + ImageAnalysis |
| Torch / flash | PASS | Real device metadata reported TORCH/FIRED |
| Zoom | PASS | Real device camera zoom ratio changed after in-app control |
| Front/rear camera switching | PASS | Real device closed camera id 0 and opened id 1 |
| Live-camera exact payload decode | PASS | On 2026-09-27 the realme RC scanner decoded known fixture `QRSCANNERS_LIVE_20260927_V2` through the live camera. The result UI displayed the exact payload and RC `files/history_records.json` persisted the same `content`, `qrType=TEXT`, `barcodeFormat=QR Code`, `isGenerated=false`. |
| WEP fallback | PASS | Connect action opened Wi-Fi Settings; no false success claim |
| WPA2 NetworkRequest path | PASS | Logcat recorded a real `WifiNetworkSpecifier` request from the RC package |
| Successful Wi-Fi association | TO VERIFY | Requires an authorized real open/WPA2/WPA3 test network |
| Release signing pipeline | PASS | CI upload-keystore secrets are configured; current-main versionCode 2 release signing verification passed in run `36294091474` |
| Signing lineage | PASS | Existing QuickQR Business key was proven to sign a different package and is explicitly rejected for automatic reuse; see `SIGNING_EVIDENCE.md` |
| Play App Signing enrollment | PASS | Play Console app created for `com.anakinyoo.qrscanners`; Play App Signing shows active with Google-managed app-signing key |
| Dedicated upload key | PASS | QR Scanners upload key created outside repo; alias `qr-scanners-upload`, RSA 4096, SHA-256 `E7:47:7E:26:10:51:E5:56:3A:6F:51:FF:FE:00:10:04:55:00:14:19:2F:BB:FE:04:93:11:74:F8:F9:1A:B6:40` |
| GitHub signing secrets | PASS | Actions secrets configured for keystore, store password, key password and alias without committing secret material |
| Signed release CI | PASS | Historical first signed release: run `36222084823` / commit `cbbe5e462110bd0a273cb4eda1111e8d85c800fb`. Current-main versionCode 2 signed release: run `36294091474` / commit `c2ed27f85a1109d12bb04dd06443344f7ca88726`; CI artifact `10923177572` produced signed APK/AAB and local `jarsigner` verification reported `jar verified`. |
| First AAB upload | PASS | Play Console accepted versionCode 1 / versionName 1.0 and the Internal testing release was published |
| Upload certificate match | PASS | Play SHA-1/SHA-256 exactly match dedicated QR Scanners upload key; see `SIGNING_EVIDENCE.md` |
| Internal tester list | PASS | `QR Scanners Internal` is selected with one verified Google account belonging to the active tester workflow |
| Internal testing rollout | PASS | Current-main version 1.0 / versionCode 2 was accepted by Play Console (min API 24+, target SDK 36), published to Internal testing on 2026-09-27, and shown as the current release available to internal testers. Historical first rollout was versionCode 1. |
| Play opt-in link | PASS | Play Console generated `https://play.google.com/apps/internaltest/4700773371089449224` |
| Play Store install from tester device | PASS | Clean Xiaomi `23078PND5G` first installed version 1.0 / versionCode 1 from Google Play, then updated through the Play client to current-main versionCode 2. After the update, `dumpsys package` reports versionName 1.0, versionCode 2, `installerPackageName=com.android.vending`, lastUpdateTime `2026-09-27 13:28:46`; `.MainActivity` launched successfully. The existing realme sideload was left untouched. |
| Current-main Play delivery | PASS | Source commit `c2ed27f85a1109d12bb04dd06443344f7ca88726` (versionCode 2) passed Android Release Gate run `36294091474`; signed AAB from artifact `10923177572` was accepted by Play as `2 (1.0)` with min API 24+ / target SDK 36, published to Internal testing, delivered as an update to the Xiaomi tester via Google Play, and launched successfully. |
| Release signing evidence | PASS | Dedicated upload key, signed CI artifact, first Play upload, and Play certificate fingerprint match all verified |
| Play release advisory warnings | PASS / N/A | Play v2 review showed two non-blocking advisories: missing R8/ProGuard deobfuscation mapping and missing native debug symbols. Release config has `isMinifyEnabled = false`, so no mapping file is generated for this build; no valid native-symbol artifact generated by this source was available. No fake artifacts were uploaded. |
| Ads SDK / Ad-Free billing implementation | GAP | Testing intentionally has no ads/billing. Production plan requires ads + one-time permanent Ad-Free, but no real AdMob app/ad-unit IDs or Play Billing product ID have been provided. Do not use fake/test production IDs; integrate and verify only after real configuration exists |
| Store listing copy | PASS | See `STORE_LISTING.md` |
| Play Store app icon | PASS | `store-assets/play_store_icon_512.png` is a valid 32-bit RGBA PNG at 512x512. The prior file had a malformed PNG data stream: System.Drawing could decode it, but Pillow and Play Console rejected it. It was re-encoded without visual redesign in commit `68da0a55295e7f3aef7328a6ac0544f78b9da1fa`; Pillow verification passed, GitHub bytes matched the verified local file, Android Release Gate run `36294091474` passed, and Play Console app `4973757644072737097` accepted the clean asset and saved the Store listing change on 2026-09-27. |
| Google Play tester-client listing icon | TO VERIFY | Play Console listing asset is saved and verified, but the tester client still renders a generic Android placeholder while the app is marked `(unreviewed)`. Re-check after listing/app review; do not treat this client placeholder as evidence that the saved Play Console icon asset is wrong. |
| Android launcher icon / OEM fallback | PASS | Density PNG legacy assets + adaptive foreground verified on realme/Oplus launcher; branded icon rendered after RC reinstall |
| Brand / logo consistency | PASS | Play Store 512 icon, Android launcher/adaptive foreground, feature graphic, and in-app splash use the same full-color cyan/purple QR brand mark. Android monochrome artwork is retained only for system themed icons. Obsolete launcher wrapper/JPG logo assets were removed; the Play icon was re-encoded as a valid PNG without changing the artwork; Android Release Gate run `36294091474` passed. |
| Feature graphic | PASS | `store-assets/feature_graphic_1024x500.jpg` is a verified 1024x500 RGB JPEG with no alpha, matching current Google Play preview-asset requirements |
| In-app privacy policy | PASS | EN/TH policy text is accessible from Settings |
| Privacy policy URL | PASS | Public HTTPS raw GitHub URL returned HTTP 200 without authentication, names QR Scanners and AnakinYoo, provides a privacy inquiry mechanism, is linked from the app, and is not a PDF |
| Data Safety final answers | PASS | Play Console saved: Diagnostics + Device or other IDs collected, not shared, encrypted in transit, required/automatic, Analytics purpose; App Content shows no remaining action-required declaration |
| Store screenshots | PASS | Six Thai phone screenshots from release UI 1.0 on the verified realme device are stored in `store-assets/screenshots/th/`; each is 1080x2160 RGB PNG with system status/navigation bars removed |
| Ads declaration | PASS | Play Console completed declaration for current Internal build: app has no ads. Must be changed to Yes before any ad-enabled Production build |
| Content rating / target audience | PASS | Play Console completed. Target ages: 13-15, 16-17, 18+. IARC ratings include PEGI 3 / Google Play 3+ / USK 0 and equivalent low-age ratings |
| Personal-account closed-test requirement | PASS (requirement identified) | This developer account's Play Console explicitly requires at least 12 opted-in closed-test testers for at least 14 days before Production Access can be requested; completion of that 12-person/14-day test is still TO VERIFY |

See `REAL_DEVICE_EVIDENCE.md` and `SIGNING_EVIDENCE.md`.

Production is not ready until every applicable GAP/TO VERIFY item has real evidence.

Current policy references:
- Target API: https://support.google.com/googleplay/android-developer/answer/11926878
- Personal-account testing: https://support.google.com/googleplay/android-developer/answer/14151465
- User Data / Privacy Policy: https://support.google.com/googleplay/android-developer/answer/10144311
- Preview assets: https://support.google.com/googleplay/android-developer/answer/9866151
- ML Kit disclosure: https://developers.google.com/ml-kit/android-data-disclosure
