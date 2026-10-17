# Phase 5: Baileys Bridge Gateway, Campaign History & Release Packaging Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement real WhatsApp Baileys Bridge client connectivity, persistent Room SQLite campaign history reporting, CSV/VCF contact import/export with custom variables (`{Company}`, `{Custom_1}`), and release-ready build configuration.

**Architecture:** A modular Baileys WebSocket/REST bridge client implements the core `WhatsAppEngine` protocol, delegating multi-device pairing and message dispatch to a configurable Baileys gateway while retaining local simulated fallback. Campaign analytics and item statuses are recorded in Room SQLite for offline auditing and CSV export, complemented by an in-app CSV contact importer. Proguard/R8 optimization rules and release signing scripts finalize the production build.

**Tech Stack:** Kotlin 2.3, Jetpack Compose (Material3), Room SQLite 2.7, OkHttp 4.12 (WebSocket & HTTP), Kotlinx Coroutines, Kotlinx Serialization.

**Spec:** Phase 5 specification covering Baileys multi-device protocol bridge, campaign persistence, CSV import/export, and release packaging.

## Global Constraints

- Android API floor: Min SDK 26, Target SDK 35, Compile SDK 35.
- Zero regression on existing Emerald Dark UI theme, spring animations, and localization (ID/TR).
- Non-destructive phonebook guarantee: Imported CSV contacts persist strictly in local Room SQLite, never overwriting native contacts.
- Strict variable preservation: Custom CSV columns map cleanly to `{Nickname}`, `{Company}`, and `{Custom_1}` variables.

## Review Focus

1. Corrupted or malformed CSV headers (missing phone column or uneven delimiters like commas vs semicolons) must display a clear, user-friendly error dialog without crashing.
2. Network failure or timeout during Baileys Bridge communication must automatically fall back or mark the queue item as Failed with descriptive error telemetry.
3. Database migration from version 1 to 2 must preserve all previously synced phonebook contacts, nicknames, and user tags.
4. Exporting large campaign CSV reports must execute asynchronously off the main thread (`Dispatchers.IO`) to avoid UI frame drops.
5. Proguard/R8 obfuscation must retain Room DAOs, Entities, and Serialization models to avoid reflection crashes in release builds.

---

### Task 1: Campaign History Database Entities & Migration

**Files:**
- Create: `app/src/main/java/com/wabulk/sender/data/local/db/CampaignEntity.kt`
- Create: `app/src/main/java/com/wabulk/sender/data/local/db/CampaignItemEntity.kt`
- Create: `app/src/main/java/com/wabulk/sender/data/local/db/CampaignDao.kt`
- Modify: `app/src/main/java/com/wabulk/sender/data/local/db/AppDatabase.kt`
- Test: `app/src/test/java/com/wabulk/sender/CampaignDatabaseTest.kt`

**Interfaces:**
- Consumes: `AppDatabase` Room architecture.
- Produces: `CampaignDao` with `insertCampaign(campaign)`, `insertItems(items)`, `observeAllCampaigns()`, `getCampaignWithItems(id)`.

- [ ] **Step 1: Write the failing test in `CampaignDatabaseTest.kt`**
Verify inserting a campaign entity and its child item entities, and verifying count queries.

- [ ] **Step 2: Run test to verify it fails**
Run: `./gradlew test --tests com.wabulk.sender.CampaignDatabaseTest`
Expected: Compilation failure or missing class.

- [ ] **Step 3: Implement `CampaignEntity.kt`, `CampaignItemEntity.kt`, `CampaignDao.kt`, and update `AppDatabase.kt`**
Add entities with auto-generated primary keys, foreign keys or relational IDs, status enums (`PENDING`, `SENDING`, `SENT`, `FAILED`), and increment database version to 2 with clean migration strategy.

- [ ] **Step 4: Run test to verify it passes**
Run: `./gradlew test --tests com.wabulk.sender.CampaignDatabaseTest`
Expected: PASS.

- [ ] **Step 5: Verify assembleDebug compiles cleanly**
Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL.

---

### Task 2: CSV Contact Importer & Campaign Report Exporter Engine

**Files:**
- Create: `app/src/main/java/com/wabulk/sender/data/io/CsvContactParser.kt`
- Create: `app/src/main/java/com/wabulk/sender/data/io/CampaignCsvExporter.kt`
- Test: `app/src/test/java/com/wabulk/sender/CsvIoTest.kt`

**Interfaces:**
- Consumes: `ContactEntity`, `CampaignEntity`, `CampaignItemEntity`.
- Produces: `CsvContactParser.parseCsv(inputStream): List<ParsedContact>`, `CampaignCsvExporter.generateCsv(campaign, items): String`.

- [ ] **Step 1: Write the failing test in `CsvIoTest.kt`**
Test parsing CSV strings with comma/semicolon separation, mapping columns (`Nama`, `No HP / Phone`, `Panggilan / Nickname`, `Company`, `Custom 1`), and test generating formatted CSV campaign reports.

- [ ] **Step 2: Run test to verify it fails**
Run: `./gradlew test --tests com.wabulk.sender.CsvIoTest`
Expected: Missing classes.

- [ ] **Step 3: Implement `CsvContactParser.kt` and `CampaignCsvExporter.kt`**
Implement robust CSV parsing handling quoted text, multiple header variations (ID/EN/TR), phone number normalization, and CSV string generation with proper RFC 4180 escaping.

- [ ] **Step 4: Run test to verify it passes**
Run: `./gradlew test --tests com.wabulk.sender.CsvIoTest`
Expected: PASS.

---

### Task 3: Baileys WebSocket/REST Bridge Client Engine

**Files:**
- Create: `app/src/main/java/com/wabulk/sender/engine/baileys/BaileysBridgeClient.kt`
- Create: `app/src/main/java/com/wabulk/sender/engine/baileys/BaileysConfig.kt`
- Modify: `app/src/main/java/com/wabulk/sender/engine/WhatsAppSessionManager.kt`
- Modify: `app/src/main/java/com/wabulk/sender/engine/queue/DispatchQueueManager.kt`
- Test: `app/src/test/java/com/wabulk/sender/BaileysBridgeTest.kt`

**Interfaces:**
- Consumes: `WhatsAppEngine` interface, `NodeSession`, `DispatchQueueManager`.
- Produces: `BaileysBridgeClient` with configurable gateway base URL (e.g. `http://10.0.2.2:3000` or remote), bearer token, HTTP/WebSocket transport, and graceful fallback to local simulated transmission.

- [ ] **Step 1: Write the failing test in `BaileysBridgeTest.kt`**
Test request serialization, pairing code endpoint response handling, and send message payload structuring.

- [ ] **Step 2: Run test to verify it fails**
Run: `./gradlew test --tests com.wabulk.sender.BaileysBridgeTest`
Expected: Class not found.

- [ ] **Step 3: Implement `BaileysConfig.kt` and `BaileysBridgeClient.kt`**
Implement the client with OkHttp, supporting REST endpoints (`/api/pair`, `/api/send`, `/api/status`, `/api/presence`) and WebSocket state notifications, integrated directly into `WhatsAppSessionManager` and `DispatchQueueManager`.

- [ ] **Step 4: Run test to verify it passes**
Run: `./gradlew test --tests com.wabulk.sender.BaileysBridgeTest`
Expected: PASS.

---

### Task 4: UI Enhancements Across Tabs

**Files:**
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/settings/SettingsScreen.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/settings/SettingsViewModel.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/contacts/ContactManagerScreen.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/contacts/ContactViewModel.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/queue/LiveQueueScreen.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/queue/QueueViewModel.kt`

**Interfaces:**
- Consumes: `BaileysConfig`, `CsvContactParser`, `CampaignCsvExporter`, `CampaignDao`.
- Produces:
  - Settings: "Baileys Bridge Gateway" configuration card (Server URL, API Key, Test Connection button).
  - Contacts: "Impor CSV" button opening a file picker or sample data importer with preview dialog.
  - Queue: "Ekspor Laporan CSV" button on campaign completion with share intent.

- [ ] **Step 1: Update ViewModels with state and logic**
Add Gateway URL/token state in `SettingsViewModel`, CSV import logic in `ContactViewModel`, and CSV export intent trigger in `QueueViewModel`.

- [ ] **Step 2: Update UI Composables with interactive components**
Add UI cards, dialogs, and buttons matching the Emerald Dark design system and localized text.

- [ ] **Step 3: Verify build with `./gradlew assembleDebug`**
Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL.

---

### Task 5: Proguard Rules, Release Packaging & Emulator Verification

**Files:**
- Modify: `app/proguard-rules.pro`
- Modify: `app/build.gradle.kts`
- Test: Verification on emulator `emulator-5554`

**Interfaces:**
- Consumes: All application layers.
- Produces: Production-ready signed or release-configured APK build.

- [ ] **Step 1: Configure Proguard rules in `app/proguard-rules.pro`**
Ensure Room, Kotlinx Serialization, OkHttp, and Coroutines keep annotations and schemas.

- [ ] **Step 2: Build Release APK**
Run: `./gradlew assembleRelease` or `./gradlew assembleDebug`
Expected: Clean build without minify/shrinker errors.

- [ ] **Step 3: Install and test on emulator `emulator-5554`**
Verify the new features live: Gateway configuration, CSV import, Campaign execution, and CSV export.

- [ ] **Step 4: Take screenshot verification artifacts**
Capture screenshots proving the functionality.
