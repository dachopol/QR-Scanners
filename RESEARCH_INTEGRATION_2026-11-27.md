# Research integration QA — 2026-11-27
Baseline: ad0015e4566aa470940929805451810446afd8ad. Previous code-bearing CI 37834529910 succeeded at 0aa910fcbab3dc8051d242d97e9d1badede1378d; baseline differs by four documentation files only.
Scope: isolated candidate; do not release or merge without full QA.

## Executed local evidence (not Android)
- Reconstructed Kotlin geo builder/parser subset, 64 deterministic latitude/longitude boundary combinations: baseline 35 pass, 29 fail; candidate 64 pass, 0 fail. Four invalid inputs rejected and two existing placeholder cases preserved on both. This is an extracted-function experiment, not full repository execution.
- Python qrcode / OpenCV 4.13.0 independent optical experiment: 9 synthetic payloads × 4 source-inspired colors × 2 margins × 6 conditions = 432. Exact decode 424; no decode 8 (all 128px downsampling); wrong payload 0. Each margin passed 212/216. Do not infer superiority of margin 4 or Android performance. Evidence JSON SHA-256 73350a4faaafa3ccc29d673dad5ee94ac14ea0506216a14a9359b9b7494f6056.
- Candidate normalizes geo output to plain decimal and preserves actual (0,0) with no query. Existing non-coordinate query placeholder remains rejected.
- Added Android/JUnit source regression for all 64 geo combinations and invalid/placeholder cases. Android candidate suite NOT RUN at authoring.

## All-module applicability
| Component | Research transfer / gate |
|---|---|
| Camera/Photo Picker | exact decoded payload, optical perturbation matrix; real ML Kit latency, focus, torch, zoom, lifecycle and low-resolution success remain pending |
| QR generator | roundtrip invariant, all 8 types, malformed input, self-decode; optical experiment independent only |
| QR actions | parser roundtrip, explicit consent, URL/Wi-Fi/SMS/contact/map handoff; physical pending |
| History | AtomicFile + dedup already present; corrupt load currently clears memory, and later persistence could overwrite original. Preserve original bytes and test recovery, crash/restart and concurrent operations before a repair |
| CSV | redaction and prefix tests exist; test whitespace/control-prefix and actual spreadsheet behavior separately |
| Settings/UI | TH/EN parity, locale restore, contrast, accessibility, compact/wide runtime QA pending |
| Privacy/security | current permissions, clipboard redaction, SDK disclosure, signing; device/network verification pending |
| Release | latest verified code CI is historical; candidate Android build and device regression, closed test and applicable monetization evidence required |

No paid API, new MCP server, deployment or release. No generated device evidence.
