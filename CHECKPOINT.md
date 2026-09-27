# QR Scanners — Checkpoint

Date: 2026-09-27  
Source of Truth: `dachopol/QR-Scanners` / `main`  
Current task: `QR-PLAY-PRODUCTION-GATE`

## Verified
- App identity: `com.anakinyoo.qrscanners`, version 1.0 / versionCode 1, targetSdk 36.
- Latest observed Android Release Gate run `36293397421` completed successfully for app/store asset source at `68da0a55295e7f3aef7328a6ac0544f78b9da1fa`; source hygiene, unit/lint/builds, AAB/APK, 16 KB alignment, signing verification, and release-evidence upload all passed. Subsequent checkpoint edits are documentation-only.
- Release AAB/signing, 16 KB alignment, Data Safety, current no-ads declaration, content rating, target audience, store assets, CameraX/torch/zoom/camera switching, Photo Picker decode, location/map handoff, WEP fallback, and WPA2 request path have recorded evidence.
- Brand/logo consistency is verified: Play Store icon, launcher/adaptive icon, feature graphic and splash use the same full-color QR mark; monochrome remains only for Android themed icons; obsolete legacy logo files were removed. The original Play icon PNG had a malformed data stream despite being readable by System.Drawing; it was re-encoded in commit `68da0a55295e7f3aef7328a6ac0544f78b9da1fa`, Pillow verification passed, GitHub matched the clean local bytes, and Play Console app `4973757644072737097` accepted and saved the clean 512x512 icon.
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
- Branding rollback point before the cleanup is `e18e43b6d3ee9a95ecf2fa55b9bd86be2acbcaa9`; splash alignment was introduced in `ba21277c15cbc343448f9ce3e181d333e9d35fa2` and final legacy-asset cleanup reached `203439177efd149f24d84edc41239f1f739fbadb`. The malformed Play icon was replaced by a valid re-encode at `68da0a55295e7f3aef7328a6ac0544f78b9da1fa`; previous asset history remains recoverable from Git.

## Definition of Done
Production may be marked ready only after every applicable item above has real evidence and the Release Gate contains no unresolved GAP / TO VERIFY items.
