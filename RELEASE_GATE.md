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
| Backup/privacy alignment | PASS | `android:allowBackup="false"` plus Android 12+ `dataExtractionRules` exclude app data from cloud backup and device transfer |
| Debug + unit + lint build | PASS | Current v3 code-bearing source passed GitHub Actions Android Release Gate run `37276458481` |
| Release AAB build | PASS | Current v3 release AAB produced successfully in CI run `37276458481` |
| 16 KB APK alignment | PASS | `zipalign -c -P 16` passed for current v3 source in CI run `37276458481` |
| Current-location source flow | PASS | Foreground coarse/fine location only; automatic Lat/Lng fill verified on real device |
| Map handoff | PASS | Decoded Geo test fixture opened Google Maps on the physical device |
| CameraX preview / analysis | PASS | Real device opened camera id 0 with Preview + ImageAnalysis |
| Torch / flash | PASS | Real device metadata reported TORCH/FIRED |
| Zoom | PASS | Real device camera zoom ratio changed after in-app control |
| Front/rear camera switching | PASS | Real device closed camera id 0 and opened id 1 |
| Live-camera exact payload decode | PASS | On 2026-09-27 the realme RC scanner decoded known fixture `QRSCANNERS_LIVE_20260927_V2` through the live camera. The result UI displayed the exact payload and RC `files/history_records.json` persisted the same `content`, `qrType=TEXT`, `barcodeFormat=QR Code`, `isGenerated=false`. |
| WEP fallback | PASS | Connect action opened Wi-Fi Settings; no false success claim |
| WPA2 NetworkRequest path | PASS | Logcat recorded a real `WifiNetworkSpecifier` request from the RC package |
| Successful Wi-Fi association | PASS | On 2026-09-27 the realme RC decoded an authorized temporary Windows Mobile hotspot WPA2 QR and requested it through `WifiNetworkSpecifier`. Android/Oplus `NetworkRequestDialogActivity` displayed the matching access point; after user selection, `cmd wifi status` reported `Supplicant state: COMPLETED`, an address in the hotspot subnet, WPA2 security, and `Requesting package name: com.anakinyoo.qrscanners.rc`. `dumpsys connectivity` reported the Wi-Fi network `CONNECTED`/`VALIDATED` with active request owned by the RC package. The temporary hotspot was then disabled, Power saving restored, the RC request released, and the device automatically returned to its previously saved Wi-Fi. Credential-bearing temporary files/history were removed after verification. |
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
| Current source v3 hardening / CI | PASS | versionCode 3 source at `5857808d929c18632134f13749d46dbf0291ed86` passed Android Release Gate run `37276458481`, including unit tests, lint, Debug/Release builds, AAB/APK, 16 KB alignment, signing verification, and artifact upload. |
| Current source v3 physical regression | TO VERIFY | v3 has not yet been re-verified on a physical realme/Xiaomi after the latest hardening. On 2026-10-09 `DESKTOP-IL7PNGM` was online and pingable, but the Desktop Commander monthly usage limit blocked execution before `adb devices -l`; therefore ADB/device readiness was not tested in that attempt. Do not replace verified Play v2 evidence with v3 until targeted scanner/history/language/Nearby-Wi-Fi regression passes on a physical device. |
| Last verified Play delivery | PASS | Source commit `c2ed27f85a1109d12bb04dd06443344f7ca88726` (versionCode 2) passed Android Release Gate run `36294091474`; signed AAB from artifact `10923177572` was accepted by Play as `2 (1.0)` with min API 24+ / target SDK 36, published to Internal testing, delivered as an update to the Xiaomi tester via Google Play, and launched successfully. |
| Release signing evidence | PASS | Dedicated upload key, signed CI artifact, first Play upload, and Play certificate fingerprint match all verified |
| Play release advisory warnings | PASS / N/A | Play v2 review showed two non-blocking advisories: missing R8/ProGuard deobfuscation mapping and missing native debug symbols. Release config has `isMinifyEnabled = false`, so no mapping file is generated for this build; no valid native-symbol artifact generated by this source was available. No fake artifacts were uploaded. |
| Ads SDK / Ad-Free billing implementation | GAP | Testing intentionally has no ads/billing. Production plan requires ads + one-time permanent Ad-Free, but no real AdMob app/ad-unit IDs or Play Billing product ID have been provided. Do not use fake/test production IDs; integrate and verify only after real configuration exists |
| Pre-Ship 20 audit | TO VERIFY | See `PRE_SHIP_20.md`: source/static audit has 11 PASS/source-PASS, 3 N/A and 6 TO VERIFY/blocked; Production remains NO until runtime/release blockers clear. |
| QR generator required-field validation | TO VERIFY | Source fix `80ce8ca259b1b12fa8010fdda1892f9c6c18f3aa` rejects empty required fields and adds TH/EN feedback plus regression tests; current CI and physical UI verification remain pending. |
| Generated-history localization | TO VERIFY | Source fix `48dc3c9acc8e2525309792da520cd748220bf913` prevents new generated history records from persisting English-only prefixes; current CI and TH/EN device verification remain pending. |
| Light-theme contrast | TO VERIFY | Source fix `6e71c0fb28294534b2baf67e1acec7bad251a065` raises the identified normal-text pairs above 4.5:1 and Android Release Gate run `37831350417` passed. Current physical light-theme visual regression remains required before this gate is PASS. |
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
| Personal-account closed-test requirement | TO VERIFY | Play Console requires at least 12 opted-in closed-test testers continuously for at least 14 days before Production Access can be requested; completion of this gate is not yet verified |

See `REAL_DEVICE_EVIDENCE.md` and `SIGNING_EVIDENCE.md`.

Production is not ready until every applicable GAP/TO VERIFY item has real evidence.

Current policy references:
- Target API: https://support.google.com/googleplay/android-developer/answer/11926878
- Personal-account testing: https://support.google.com/googleplay/android-developer/answer/14151465
- User Data / Privacy Policy: https://support.google.com/googleplay/android-developer/answer/10144311
- Preview assets: https://support.google.com/googleplay/android-developer/answer/9866151
- ML Kit disclosure: https://developers.google.com/ml-kit/android-data-disclosure
