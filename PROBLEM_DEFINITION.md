# QR Scanners — Problem Definition

Status: current-scope baseline derived from the implemented source and repository evidence. This document does not claim to reconstruct undocumented historical research.

## Problem

Android users need a reliable way to scan QR codes and common barcodes from the live camera or a selected image, understand the decoded content, take the appropriate Android action, and create QR codes without sending normal scan/history content to a developer backend.

## Target user

Android users who need routine QR/barcode scanning and QR creation, including Thai and English users.

## Core use cases

1. Scan a QR/barcode with the live camera.
2. Scan a selected image through Android Photo Picker.
3. Decode the exact payload and classify common content types.
4. Take a safe system action for URL, Wi-Fi, contact, phone, SMS, email, or geo content.
5. Generate QR codes for supported content types.
6. Keep local scan/generation history, favorites, search, delete/clear and CSV export.
7. Use the app in Thai or English and across compact/wide Android layouts.

## Success criteria

- Exact known QR payload can be decoded through live camera and Photo Picker.
- Camera permission denial does not block image-based scanning.
- Torch, zoom and front/rear camera controls function on supported hardware.
- Wi-Fi QR actions never report a false successful connection; unsupported or denied flows fall back safely.
- Sensitive Wi-Fi credentials are not exposed in history previews or CSV export.
- Core history/preferences remain local to app-private storage unless the user explicitly shares/opens content.
- TH/EN resources stay in parity.
- Current source passes unit tests, lint, Debug/Release build, AAB build, release signing verification and 16 KB alignment gate.
- Production is not claimed until all applicable Play Console and physical-device release gates have real evidence.

## Scope

- Android application, package `com.anakinyoo.qrscanners`.
- Kotlin + Jetpack Compose.
- CameraX + Google ML Kit barcode scanning.
- Android Photo Picker.
- Local app-private history/settings.
- Android system/external-app handoffs for supported actions.

## Not in current verified production scope

- User accounts.
- Developer-operated backend.
- Ads SDK / Play Billing production implementation until real configuration exists.
- Any fabricated telemetry, tester evidence, ad IDs, billing IDs or network results.

## Evidence

See `README.md`, `RELEASE_GATE.md`, `REAL_DEVICE_EVIDENCE.md`, `DATA_SAFETY.md`, and current source under `app/`.
