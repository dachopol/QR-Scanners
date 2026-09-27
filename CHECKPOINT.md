# QR Scanners — Checkpoint

Date: 2026-09-27  
Source of Truth: `dachopol/QR-Scanners` / `main`  
Current task: `QR-PLAY-PRODUCTION-GATE`

## Verified
- App identity: `com.anakinyoo.qrscanners`, version 1.0 / versionCode 1, targetSdk 36.
- Latest observed Android Release Gate run `36290609985` completed successfully for app source at `5423b635791d26f27d92275878e3b32fd2f156a4`; source hygiene, unit/lint/builds, AAB/APK, 16 KB alignment, signing verification, and release-evidence upload all passed. Subsequent checkpoint edits are documentation-only.
- Release AAB/signing, 16 KB alignment, Data Safety, current no-ads declaration, content rating, target audience, store assets, CameraX/torch/zoom/camera switching, Photo Picker decode, location/map handoff, WEP fallback, and WPA2 request path have recorded evidence.
- Connected physical device: realme RMX3241. Both release and `.rc` packages are present.
- The installed release currently reports installer source `pc`, so it is not evidence of a Play Store install. Google Play tester authentication remains unresolved; some `market://` intents on the realme are handled by Oplus/HeyTap.
- The connected Android 17 emulator has Play Store but is not signed in.

## Remaining blockers / verification
1. Live-camera exact payload decode on the physical device. A 2026-09-27 attempt displayed `QRSCANNERS_LIVE_20260927` while the RC scanner was active, but the exact payload was not found in RC history, so this remains unverified.
2. Successful association with an authorized real open/WPA2/WPA3 Wi-Fi network.
3. Authenticate the configured tester account and complete install through Play Internal testing.
4. Complete the developer account's required closed test: at least 12 opted-in testers for at least 14 days before Production Access.
5. Production monetization is still blocked: the plan is real ads + one-time permanent Ad-Free, but no real AdMob app/ad-unit IDs or Play Billing product ID have been provided. After real configuration exists, integrate it, verify purchase/restore and ad suppression on a real device, then re-check Data Safety, Ads and Billing declarations.

## Safety / rollback
- Do not uninstall the current release package merely to force a Play install unless app data is safely backed up or the user explicitly accepts data loss. The current sideloaded build and Play-distributed build may have different signing certificates due to Play App Signing.
- Do not add fake AdMob IDs, fake billing products, fake network results, or fabricated test evidence.

## Definition of Done
Production may be marked ready only after every applicable item above has real evidence and the Release Gate contains no unresolved GAP / TO VERIFY items.
