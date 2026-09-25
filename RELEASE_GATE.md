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
| Camera / Torch / Zoom | TO VERIFY | Physical Android device |
| Current location / Map handoff | TO VERIFY | Physical Android device with Location enabled |
| Wi-Fi connect action | TO VERIFY | Physical Android device/network |
| Store screenshots / content rating / ads status | TO VERIFY | Play Console + real release UI |
| Testing-track requirement | TO VERIFY | Confirm actual developer-account eligibility/status |

Production is not ready until every applicable GAP/TO VERIFY item has real evidence.

Current policy references:
- Target API: https://support.google.com/googleplay/android-developer/answer/11926878
- Preview assets/icon: https://support.google.com/googleplay/android-developer/answer/9866151
- ML Kit disclosure: https://developers.google.com/ml-kit/android-data-disclosure
