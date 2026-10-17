# Phase 4 Implementation Plan: WhatsApp Dual-Node Pairing Engine, Foreground Service, & Full Interactivity Audit

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement Phase 4 of the WhatsApp Bulk Sender (Dual-Node WhatsApp Linked-Device Session Engine with 8-digit pairing codes, Android Foreground Service with live notification) and fulfill the user requirement that **every single clickable element across all 4 screens is fully functional and interactive**.

**Architecture:** 
- `WhatsAppEngine` & `WhatsAppSessionManager` coordinate Node 1 (Personal) and Node 2 (Business) session states, 8-digit pairing code generation (`K97B-42VX`), countdown expiration, clipboard integration, and WhatsApp package deep-linking.
- `WhatsAppForegroundService` runs as an Android Foreground Service with notification channels, wake-lock safety, and real-time progress broadcast (`NotificationHelper`).
- `DispatchQueueManager` orchestrates the two-tier anti-ban transmission pipeline (typing indicator simulation + Gaussian jitter delay) feeding `QueueViewModel` so the Live Queue tab reflects actual contacts and message bubbles launched from Composer.
- Complete UI Interactivity Audit across all screens:
  - **Composer:** Help dialog, variable insertion into active bubble, media attachment add/remove, interactive jitter presets/slider, AI rewrite tone integration, broadcast launch with validation.
  - **Contacts:** "+ Tag Baru" dialog creating real Room tags, tag assignment dialog per contact, inline nickname editing, quick format pills, selection toggles.
  - **Queue:** Dynamic `QueueViewModel` with live filtering (Semua, Aktif, Terkirim, Menunggu), Pause/Resume, Stop Blast, progress tracking.
  - **Settings:** Real session disconnect/reconnect, 8-digit pairing code regeneration with countdown timer, Copy & Open WhatsApp intent, interactive anti-ban sliders, switches, batch cooldown steppers, and AI tone selectors.

**Tech Stack:** Kotlin Coroutines, StateFlow, Android Foreground Service, Android NotificationManager, Android ClipboardManager, Intent deep-linking, Jetpack Compose Material 3, Room SQLite, JUnit 4.

**Spec:** `plan.md`, `2026-10-17-wa-bulk-sender-design copy.md`

## Global Constraints

- Design System Fidelity: Emerald Cybernetic tokens (`#0F1419`, `#25D366`, `#4EDE93`, `#00A572`, `#DEE3EA`, `#30353B`).
- Phonebook Non-Destructive Guarantee: All contact tags, selections, and nicknames persist in local Room DB only.
- Variable Protection: `{Nickname}`, `{Company}`, `{Custom_1}` and spintax formatting preserved across all flows.
- Every clickable button, chip, icon, and switch MUST perform its action with clear visual or functional feedback — no empty `{ }` lambdas.

## Review Focus

1. **8-Digit Pairing Code Flow:** Generating code format `XXXX-XXXX`, countdown ticking down every second, "Salin & Buka WhatsApp" copying code to clipboard and launching WhatsApp package intent.
2. **Foreground Service Lifecycle:** Starts with ongoing notification, displays current progress, and cleanly terminates on completion or stop.
3. **Queue Launch from Composer:** Clicking "Luncurkan Broadcast Massal" validates that contacts are selected, builds queue items, navigates to Queue tab, and starts transmission simulation with live countdown.
4. **Tag Management in Contacts:** "+ Tag Baru" dialog saves new tag in Room DB; clicking tag icon on contact allows adding/removing tags dynamically.
5. **Settings Interactivity:** All switches, steppers (+/- cooldown), delay presets, and ping/disconnect actions update state predictably.

---

### Task 1: WhatsApp Session Manager & Dual-Node Pairing Engine (Task 4.2)

**Files:**
- Create: `app/src/main/java/com/wabulk/sender/engine/model/NodeStatus.kt`
- Create: `app/src/main/java/com/wabulk/sender/engine/model/NodeSession.kt`
- Create: `app/src/main/java/com/wabulk/sender/engine/WhatsAppEngine.kt`
- Create: `app/src/main/java/com/wabulk/sender/engine/WhatsAppSessionManager.kt`
- Create: `app/src/test/java/com/wabulk/sender/WhatsAppSessionTest.kt`

- [ ] **Step 1: Write unit tests in `WhatsAppSessionTest.kt`**
  Test 8-digit pairing code generation format `[A-Z0-9]{4}-[A-Z0-9]{4}`, session status transitions (`DISCONNECTED` -> `PAIRING` -> `CONNECTED`), and node rotation logic.
- [ ] **Step 2: Create `NodeStatus.kt` and `NodeSession.kt`**
  Define `NodeStatus` (CONNECTED, PAIRING, DISCONNECTED, ERROR), `AccountNode` (PERSONAL, BUSINESS, DUAL_SYNC), and `NodeSession` data class (id, nodeType, phoneNumber, status, pairingCode, codeExpiresAt, uptimeSeconds, healthPercent).
- [ ] **Step 3: Implement `WhatsAppEngine.kt` and `WhatsAppSessionManager.kt`**
  Provide methods: `generatePairingCode(nodeType)`, `connectNode(nodeType, phone)`, `disconnectNode(nodeType)`, `pingNode(nodeType)`, observe session flows for Node 1 and Node 2.
- [ ] **Step 4: Run tests to verify session manager passes**
  Run: `./gradlew test --tests com.wabulk.sender.WhatsAppSessionTest`

---

### Task 2: Android Foreground Service & Notification Management (Task 4.1)

**Files:**
- Create: `app/src/main/java/com/wabulk/sender/service/NotificationHelper.kt`
- Create: `app/src/main/java/com/wabulk/sender/service/WhatsAppForegroundService.kt`
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Add permissions and Service declaration in `AndroidManifest.xml`**
  Add `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_DATA_SYNC`, `POST_NOTIFICATIONS`, `WAKE_LOCK`. Declare `WhatsAppForegroundService` with `foregroundServiceType="dataSync"`.
- [ ] **Step 2: Implement `NotificationHelper.kt`**
  Create notification channel `wa_bulk_dispatch_channel`, notification builder for ongoing transmission progress (e.g., "Mengirim 18/42 (43%) - Jeda berikutnya 34d").
- [ ] **Step 3: Implement `WhatsAppForegroundService.kt`**
  Handle `ACTION_START`, `ACTION_PAUSE`, `ACTION_RESUME`, `ACTION_STOP` intents; manage WakeLock; update ongoing notification with live progress.

---

### Task 3: Dispatch Queue Engine & Live Queue State Integration (Phase 5 / Connecting Composer & Queue)

**Files:**
- Create: `app/src/main/java/com/wabulk/sender/engine/queue/model/QueueItem.kt`
- Create: `app/src/main/java/com/wabulk/sender/engine/queue/DispatchQueueManager.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/queue/QueueViewModel.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/queue/LiveQueueScreen.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/MainScreen.kt`

- [ ] **Step 1: Create `QueueItem.kt` and `DispatchQueueManager.kt`**
  Define `QueueItem` (id, contactId, recipientName, phoneNumber, bubbleTexts, nodeUsed, status: PENDING, TYPING, DELIVERED, FAILED, sentTimestamp).
  Implement `DispatchQueueManager` singleton with StateFlows for `queueItems`, `isPaused`, `isRunning`, `jitterCountdownSeconds`, `currentTypingContact`, `sentCount`, `totalCount`.
- [ ] **Step 2: Implement `QueueViewModel.kt`**
  Expose filtered queue items by status (ALL, ACTIVE, DELIVERED, PENDING), pause/resume actions, and stop blast action.
- [ ] **Step 3: Update `LiveQueueScreen.kt`**
  Bind UI to `QueueViewModel`: real % progress, dynamic countdown numbers, live item list with status badges, working Pause/Resume button, working Stop button with confirmation.
- [ ] **Step 4: Update `MainScreen.kt`**
  Pass shared queue manager / view model so "Luncurkan Broadcast Massal" enqueues selected contacts and navigates to Queue tab seamlessly.

---

### Task 4: Complete Interactivity Audit & Implementation for Tab 1 (Composer) & Tab 2 (Contacts)

**Files:**
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/composer/ComposerScreen.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/composer/ComposerViewModel.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/contacts/ContactManagerScreen.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/contacts/ContactViewModel.kt`

- [ ] **Step 1: Wire all Composer interactive elements**
  - Variable pills (`+{Nickname}`, `+{Company}`, `+{Custom_1}`): insert token into the active bubble text.
  - Help icon (?): opens a dialog explaining variables, Spintax syntax, and Anti-ban guardrails.
  - Media Attachment: "+ Lampirkan" launches device file picker or sample attachments; remove button deletes item.
  - Anti-Ban Level 3 Shield badge: opens dialog explaining safe sending limits and jitter rules.
  - "Luncurkan Broadcast Massal": validates at least 1 contact is selected. If none, shows dialog "Pilih kontak terlebih dahulu di Tab Kontak". If valid, prepares queue tasks and switches to Queue tab.
- [ ] **Step 2: Wire all Contacts interactive elements**
  - "+ Tag Baru": opens an interactive dialog to enter a new tag name, saving it to Room DB via `ContactRepository.createTag(name)`.
  - Tag icon on each contact card: opens a bottom sheet or dialog to assign/unassign tags to that contact.
  - Checkbox, Select All in Tag, Quick format pills, Inline nickname editor: verify all update Room DB and reflect in UI.

---

### Task 5: Complete Interactivity Audit & Implementation for Tab 4 (Settings)

**Files:**
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/settings/SettingsViewModel.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/settings/SettingsScreen.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/MainScreen.kt`

- [ ] **Step 1: Create `SettingsViewModel.kt`**
  Hold settings state: active delay preset, delay range (min/max), typing sim toggle, auto-rotate toggle, cooldown minutes & batch size, default AI tone, and session manager reference.
- [ ] **Step 2: Wire SettingsScreen buttons and controls**
  - Node 1: "Test Ping" pings the engine; "Putuskan" disconnects node session; "Hubungkan" initiates pairing.
  - Node 2: "Minta Kode Pairing" generates real new 8-digit code; timer resets to 180s.
  - "Salin & Buka WhatsApp": copies code to system ClipboardManager with Toast feedback AND launches WhatsApp package intent (`com.whatsapp` or `com.whatsapp.w4b`).
  - Anti-ban delay slider / presets: selecting Konservatif, Optimal, Turbo updates min/max delay and slider position.
  - Toggles: "Simulasi Mengetik" and "Rotasi Mesin Otomatis" toggle on/off.
  - Cooldown steppers: `+` and `-` buttons increment and decrement minutes (e.g. 5m) and message count (e.g. 25).
  - Default AI Tone chips: "Santai", "Ramah", "Formal" switch active state.

---

### Task 6: End-to-End Verification on Android Emulator & Unit Tests

**Files:**
- Test: Full `./gradlew testDebugUnitTest` suite
- Verify on `emulator-5554`:
  - Open Tab Kontak -> click "+ Tag Baru" -> create tag "VIP" -> tag a contact.
  - Open Tab Composer -> set variables -> click "Luncurkan Broadcast Massal" -> transitions to Tab Antrean with active countdown and live items!
  - Click "Jeda Antrean" -> pauses; click "Lanjutkan" -> resumes.
  - Open Tab Pengaturan -> click "Salin & Buka WhatsApp" -> verifies copy and intent feedback; toggle switches and steppers.
  - Capture verification screenshots for artifacts.
