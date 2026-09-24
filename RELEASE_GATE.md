# QR Scanners — Release Gate

Source of Truth: `main`. GitHub Actions status for the current HEAD is authoritative; this file intentionally does not pin a commit SHA.

| Gate | Status | Evidence / next action |
|---|---|---|
| Package identity | PASS | `com.anakinyoo.qrscanners` |
| targetSdk | PASS | 36 |
| Photo Picker | PASS | `ActivityResultContracts.PickVisualMedia` |
| Old/unused app SDK cleanup | PASS | Firebase/Retrofit/OkHttp/Moshi/KSP/old namespaces removed from active app config |
| ML Kit Data Safety disclosure | PASS | Privacy/Data Safety docs include ML Kit diagnostics/usage telemetry |
| Backup/privacy alignment | PASS | `android:allowBackup="false"` |
| Debug + unit + lint build | PASS | Latest verified GitHub Actions run completed successfully |
| Release AAB build | PASS | Unsigned release AAB produced by `qr-scanners-release-gate` artifact |
| 16 KB APK alignment | PASS | `zipalign -c -P 16` passed in latest verified CI |
| Current-location source flow | PASS | Optional foreground coarse/fine location + automatic Lat/Lng fill; no background location |
| Release signing | TO VERIFY | Requires real upload keystore secrets; never commit keys |
| Camera / Torch / Zoom | TO VERIFY | Physical Android device |
| Current location / Map handoff | TO VERIFY | Physical Android device with Location enabled |
| Wi-Fi connect action | TO VERIFY | Physical Android device/network |
| Store listing / screenshots / content rating / ads status | TO VERIFY | Play Console |
| Closed test requirement | TO VERIFY | Confirm actual developer-account eligibility/status |

Production is not ready until every applicable TO VERIFY item has real evidence.
