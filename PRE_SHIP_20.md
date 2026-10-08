# QR Scanners — Universal Pre-Ship 20 Check

Date: 2026-10-09
Source: current `main` source/config + recorded release/device evidence.
Rule: source-only evidence is not substituted for runtime evidence.

| # | Check | Status | Evidence / remaining work |
|---|---|---|---|
| 1 | Design — color consistency | PASS | Central Light/Dark Material3 schemes in `ui/theme/Theme.kt`; brand assets are recorded consistent in `RELEASE_GATE.md`. |
| 2 | Design — typography hierarchy | PASS | UI uses MaterialTheme typography roles across headings/body/labels; no direct hard-coded visible `Text("...")` detected by CI hygiene. |
| 3 | Design — spacing/padding consistency | TO VERIFY | Compose source uses shared dp spacing patterns and Material components, but final visual consistency requires current-version screenshot/device review. |
| 4 | Design — contrast/readability | FIXED / TO VERIFY | Light primary/white was ~4.10:1 and variant text ~4.34:1. Source changed to `#0369A1` (~5.93:1 with white) and `#475569` on `#F1F5F9` (~6.92:1). CI + physical visual check remain required. |
| 5 | Mobile — no horizontal overflow | TO VERIFY | Adaptive source exists; current v3 physical regression/screenshot pass is pending. |
| 6 | Mobile — tap targets | PASS (source) | Scanner action controls include 52dp controls; history quick actions were hardened to 48dp; Material buttons/navigation provide standard touch surfaces. |
| 7 | Mobile — responsive support matrix | TO VERIFY | Compact bottom navigation and >=600dp NavigationRail source paths exist; current-v3 physical/device-matrix confirmation is pending. |
| 8 | Mobile — mobile text readability | TO VERIFY | TH/EN keys are in parity (131/131 at latest static check); current visual/device verification remains pending. |
| 9 | States — loading | PASS (source) | Splash/loading state and current-location `isLocating` state are implemented. |
| 10 | States — empty | PASS (source) | History/favorites empty states are implemented with localized resources. |
| 11 | States — error/validation | PASS (source) | Camera/image/location/Wi-Fi/external-action error and denial states exist in localized resources and source flows. |
| 12 | States — disabled/success | PASS (source) | Current-location action disables while locating; success feedback such as location-loaded/copy states is implemented. |
| 13 | Real User — auth/login | N/A | Current app has no account/authentication system. |
| 14 | Real User — core flow | TO VERIFY | v2 live-camera, Photo Picker and Play install evidence exists; current v3 physical regression is pending. |
| 15 | Real User — payment/critical external | N/A / BLOCKED FUTURE | Current internal build has no billing/ads. Production monetization is not implemented without real AdMob/Play Billing configuration. External URL/map/contact/Wi-Fi flows have source and historical device evidence. |
| 16 | Real User — edge cases | TO VERIFY | WEP fallback, network unavailable, permission denial and local privacy hardening exist; new Android 13+ Nearby Wi-Fi permission flow still needs physical regression. |
| 17 | Launch — value proposition | PASS (source) | Scan/Create/History/Settings IA and QR/barcode scanner title communicate the core app purpose. |
| 18 | Launch — primary CTA/core action | PASS (source) | Camera scanning is primary; explicit camera permission and gallery fallback are available. |
| 19 | Launch — trust/legal | PASS | In-app TH/EN Privacy Policy, public privacy URL, Data Safety, signing and store declarations are recorded. |
| 20 | Launch — SEO/metadata | N/A (Android native) | Web SEO is not applicable to the native Android app; Google Play listing metadata/assets are tracked separately in `STORE_LISTING.md` / `RELEASE_GATE.md`. |

## Result

- PASS/source-PASS: 11
- N/A: 3
- TO VERIFY / blocked: 6
- Production Ready: **NO**

Primary blockers:
1. Current v3 physical regression, including Android 13+ Nearby Wi-Fi permission/connection/cleanup.
2. Current light-theme visual verification after the contrast fix.
3. Google Play closed-test requirement (at least 12 opted-in testers continuously for at least 14 days for this eligible account).
4. If production ads/Ad-Free are enabled, real production configuration plus billing/ad-suppression and Play declaration re-verification.
