# QR Scanners — Release Gate

| Gate | Status | Evidence / next action |
|---|---|---|
| Package identity | PASS | `com.anakinyoo.qrscanners` |
| targetSdk | PASS | 36 |
| Photo Picker | PASS | `ActivityResultContracts.PickVisualMedia` |
| Unused Firebase/network app SDKs | PASS | Removed from app module |
| ML Kit Data Safety disclosure | FIXED | Docs no longer claim zero collection |
| Backup/privacy alignment | PASS | `android:allowBackup="false"` |
| Debug + unit + lint build | TO VERIFY | GitHub Actions |
| Release AAB build | TO VERIFY | GitHub Actions |
| Release signing | TO VERIFY | Requires real upload keystore secrets; never commit keys |
| 16 KB APK alignment | TO VERIFY | GitHub Actions `zipalign -P 16` |
| Camera / Torch / Zoom | TO VERIFY | Physical Android device |
| Wi-Fi connect action | TO VERIFY | Physical Android device/network |
| Store listing / screenshots / content rating / ads status | TO VERIFY | Play Console |
| Closed test requirement | TO VERIFY | Applies to eligible new personal accounts; confirm account status |

Production may be marked ready only when every applicable TO VERIFY item has real evidence.
