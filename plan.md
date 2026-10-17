# WhatsApp Bulk Sender Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a robust, high-performance Android bulk WhatsApp broadcast application featuring Google Stitch's Emerald Cybernetic theme, tactile spring animations, bilingual localization (Indonesian & Turkish), native phonebook sync to isolated local SQLite/Room database, custom nicknames, tag filtering, multi-bubble message composition with media attachments, Cloudflare Worker AI bridge (Groq/Gemini), dual-node WhatsApp linked-device foreground engine via 8-digit pairing codes, and a two-tier anti-ban jitter queue.

**Architecture:** Modern Android MVVM + Clean Architecture built with Kotlin and Jetpack Compose (Material 3). Room Database handles isolated local contacts, tags, and queue persistence. A persistent Android Foreground Service maintains the dual WhatsApp linked-device protocol sessions without OS battery killer interference. A two-tier rate-limiting dispatch engine coordinates typing indicators and Gaussian jitter delays. OkHttp/Retrofit communicates with Cloudflare Worker for AI text polishing and spintax generation while strictly locking variable tokens like `{Nickname}`.

**Tech Stack:** 
- Language: Kotlin 1.9+ / 2.0+
- UI Framework: Jetpack Compose with Material 3 & Emerald Cybernetic Tokens
- Physics & Animation: Compose Animation with Spring Spec (`stiffnessMediumLow`, `dampingRatioLowBouncy`)
- Database: Room SQLite (`androidx.room`)
- Concurrency & Reactive Streams: Kotlin Coroutines & `StateFlow`
- Background Processing: Android Foreground Service with Ongoing Notification & WakeLock/Battery Saver exemption
- Networking: OkHttp 4 / Retrofit 2 for Cloudflare Worker AI API
- Internationalization: Dynamic bilingual localization (Indonesian `in-ID` and Turkish `tr-TR`)
- Android Target: Min SDK 26 (Android 8.0 Oreo), Target SDK 35 (Android 15)

**Spec:** `2026-10-17-wa-bulk-sender-design copy.md` and `stitch_whatsapp_bulk_campaign_composer/emerald_cybernetic_automation/DESIGN.md`

---

## Global Constraints

- **Design System Fidelity:** Rigid adherence to Google Stitch's Emerald Cybernetic tokens (`#0F1419` base dark surface, `#25D366` primary emerald, `#4EDE93` secondary accent, `#00A572` container, `#DEE3EA` text on dark, `#F8FAFC` light canvas option).
- **Phonebook Non-Destructive Guarantee:** Never alter, update, or overwrite native Android system contacts in `ContactsContract`. All nicknames and tags exist exclusively in the application's local Room SQLite database.
- **Variable Protection:** The `{Nickname}` token and spintax variables must never be translated, altered, or deleted during AI processing or language switching.
- **Two-Tier Anti-Ban Delays:** 
  - Tier 1: 3-6 second typing indicator simulation between bubbles for the same recipient.
  - Tier 2: 25-60 second randomized Gaussian jitter delay between different recipients.
  - Batch cooldown: mandatory quiet pause (e.g. 5 minutes every 25 messages).
- **Dual-Session Support:** Seamless switching and round-robin dispatching between WhatsApp Personal and WhatsApp Business nodes.
- **No External Paid API Dependencies:** Direct WhatsApp linked-device protocol using 8-digit pairing code on the same physical phone (no secondary phone camera or QR code required).
- **Bilingual Dynamic Switch:** Immediate runtime language toggle between Bahasa Indonesia and Türkçe without application process restart.

---

## Review Focus

1. **Language Switching Re-render:** Toggling language in Settings must immediately update all strings across all 4 tabs without state destruction or lost unsaved draft data.
2. **Spring Animation Fluidity:** Tab switching, card touch down, chip selection, and modal bottom sheet interactions must use physics-based springs without frame drops (60/120fps).
3. **Contact Sync Deduplication:** Syncing contacts from phonebook must merge gracefully without duplicating records or clearing existing user-entered custom nicknames and category tags.
4. **Foreground Service Surviving Doze Mode:** The Android Foreground Service must stay alive with ongoing notification when screen is off or app is swiped from recent tasks.
5. **Queue Pause & Emergency Abort:** Pressing Pause or Stop during active dispatch must instantly halt the pipeline, cancel scheduled jitter timers, and cleanly flush typing presence.

---

## Phase 1: Kerangka UI Stitch, Animasi Pegas, dan Lokalisasi Bahasa Indonesia-Turki

### Task 1.1: Project Scaffolding & Emerald Cybernetic Design System Setup
**Files:**
- Create/Modify: `app/build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml`
- Create: `app/src/main/java/com/wabulk/sender/ui/theme/Color.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/theme/Type.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/theme/Theme.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/theme/Animation.kt`
- Test: `app/src/test/java/com/wabulk/sender/ThemeTest.kt`

**Interfaces:**
- Consumes: Google Stitch `DESIGN.md` design tokens.
- Produces: `EmeraldTheme`, `EmeraldColors`, `SpringSpecs`, `InterTypography`.

- [x] **Step 1: Initialize project using android CLI and configure Gradle dependencies**
  Configure Compose Material 3, Navigation Compose, Material Symbols icons, and Spring physics dependencies.
- [x] **Step 2: Implement Emerald Cybernetic color palette and typography in `Color.kt` and `Type.kt`**
  Map `#0F1419`, `#171C21`, `#25D366`, `#4EDE93`, `#00A572`, `#DEE3EA`, `#30353B`, `#869584`.
- [x] **Step 3: Define reusable Spring physics specifications in `Animation.kt`**
  Provide `BouncySpringSpec` (damping `0.65f`, stiffness `Spring.StiffnessMediumLow`), `SmoothSpringSpec`, and pulse modifier.
- [x] **Step 4: Verify theme compilation and preview rendering**
  Run: `./gradlew test` or build check.

### Task 1.2: Dynamic Bilingual Localization Engine (ID & TR)
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/data/locale/AppLanguage.kt`
- Create: `app/src/main/java/com/wabulk/sender/data/locale/LocalizationManager.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/locale/AppStrings.kt`
- Modify: `app/src/main/res/values/strings.xml` (Indonesian default)
- Create: `app/src/main/res/values-tr/strings.xml` (Turkish)
- Test: `app/src/test/java/com/wabulk/sender/LocalizationTest.kt`

**Interfaces:**
- Consumes: Language selection state (`AppLanguage.INDONESIAN`, `AppLanguage.TURKISH`).
- Produces: `LocalStrings.current`, `LocalizationManager.setLanguage(lang)`.

- [x] **Step 1: Write unit tests for language translation dictionary and token preservation**
  Verify that all keys exist for both languages and that `{Nickname}` placeholder is untouched.
- [x] **Step 2: Implement `AppStrings` interface and concrete dictionaries for Indonesian and Turkish**
  Translate all terms (Compose -> Buat Pesan / Mesaj Oluştur, Contacts -> Kontak / Kişiler, Queue -> Antrean / Kuyruk, Settings -> Pengaturan / Ayarlar, etc.).
- [x] **Step 3: Implement `LocalizationManager` with `MutableStateFlow<AppLanguage>`**
  Provide Compose `CompositionLocalProvider(LocalStrings provides strings)`.
- [x] **Step 4: Verify localization test suite passes**
  Run: `./gradlew test --tests com.wabulk.sender.LocalizationTest`

### Task 1.3: 4-Tab Navigation Shell with Spring Transitions
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/ui/navigation/Screen.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/navigation/EmeraldBottomNav.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/MainScreen.kt`
- Create: `app/src/main/java/com/wabulk/sender/MainActivity.kt`
- Test: `app/src/test/java/com/wabulk/sender/NavigationTest.kt`

**Interfaces:**
- Consumes: `AppStrings`, `BouncySpringSpec`.
- Produces: `MainScreen` holding 4 tabs with animated slide/fade transitions and bottom nav bar with active emerald pill and glowing underline indicator.

- [x] **Step 1: Write test verifying navigation routes and tab destination states**
- [x] **Step 2: Implement `EmeraldBottomNav` matching Stitch HTML structure**
  Include active pill background, animated emerald underline dot, queue badge counter (4), and tactile spring click response.
- [x] **Step 3: Implement `MainScreen` hosting `AnimatedContent` with `slideInHorizontally` + `spring`**
- [x] **Step 4: Verify navigation transitions without crash**

### Task 1.4: Screen 1 Shell - Campaign Composer
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/composer/ComposerScreen.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/composer/components/AccountSwitcherPill.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/composer/components/MediaCarousel.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/composer/components/SpintaxVariableBar.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/composer/components/WhatsAppPreviewBubble.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/composer/components/JitterSliderGauge.kt`

**Interfaces:**
- Consumes: Design tokens, `LocalStrings`.
- Produces: Fully interactive Composer Screen UI matching `campaign_composer/code.html`.

- [x] **Step 1: Implement top header with Live Pulse status dot and Anti-Ban Level 3 Shield**
- [x] **Step 2: Implement Segmented Account Switcher Pill (WA Personal / WA Business / Dual Sync)**
- [x] **Step 3: Implement Media Attachment Carousel with +Attach button and thumbnail cards**
- [x] **Step 4: Implement Spintax Dynamic Variable insertion pills (`+{Nickname}`, `+{Company}`, etc.)**
- [x] **Step 5: Implement WhatsApp Real-Time Recipient Preview card with dark green chat bubble and double blue ticks**
- [x] **Step 6: Implement Dynamic Send Jitter gauge and "Launch Bulk Broadcast" emerald button**

### Task 1.5: Screen 2 Shell - Contact Manager & Nicknames
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/contacts/ContactManagerScreen.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/contacts/components/TelemetryShelf.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/contacts/components/FilterChipRow.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/contacts/components/ContactCardItem.kt`

**Interfaces:**
- Consumes: Design tokens, `LocalStrings`.
- Produces: Contact Manager screen matching `contact_manager_nicknames/code.html`.

- [x] **Step 1: Implement Telemetry Metric Shelf (Total: 1,280, Selected: 42, Nicknames: 38/42)**
- [x] **Step 2: Implement Sticky Search bar with instant clear icon**
- [x] **Step 3: Implement horizontal scrolling Tag Filter Chips (All, Keluarga, Teman, Bisnis, +New Tag)**
- [x] **Step 4: Implement "Select All in [Active Tag]" checkbox toggle with emerald checkmark**
- [x] **Step 5: Implement Contact Card Item with squircle checkbox, avatar, phone number, inline nickname input, preview string, and quick-format micro-pills**

### Task 1.6: Screen 3 Shell - Live Sending Queue
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/queue/LiveQueueScreen.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/queue/components/QueueTelemetryCard.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/queue/components/TypingJitterBanner.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/queue/components/QueueItemRow.kt`

**Interfaces:**
- Consumes: Design tokens, `LocalStrings`.
- Produces: Live Sending Queue screen matching `live_sending_queue/code.html`.

- [x] **Step 1: Implement Campaign Hero Progress Card (18/42 Sent, 43% Complete, segmented progress bar with pulsing thumb)**
- [x] **Step 2: Implement Typing / Jitter Delay Status Banner with rotating radar icon, bouncing typing indicator dots, and countdown timer**
- [x] **Step 3: Implement Tactical Control Buttons (Pause Queue in amber/grey, Stop Blast in error red container)**
- [x] **Step 4: Implement Filter & Search Strip (All, Active, Delivered, Pending)**
- [x] **Step 5: Implement Queue Recipient Items with status tags and timestamps**

### Task 1.7: Screen 4 Shell - Accounts & Safety Settings with Language Switcher
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/settings/SettingsScreen.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/settings/components/LanguageSelectorCard.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/settings/components/ConnectedEngineCard.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/settings/components/AntiBanRulesCard.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/settings/components/PairingCodeDisplay.kt`

**Interfaces:**
- Consumes: `LocalizationManager`, design tokens.
- Produces: Settings screen matching `accounts_safety_settings/code.html` with working Indonesian & Turkish switcher.

- [x] **Step 1: Implement Language Switcher Card with Indonesian (ID) and Turkish (TR) pills that trigger instant UI re-render**
- [x] **Step 2: Implement Connected Engines cards (Node 1 WA Personal Connected, Node 2 WA Business Pairing Ready)**
- [x] **Step 3: Implement 8-Digit Pairing Code Display (`K97B-42VX`) with countdown timer (02:45) and Copy Code button with feedback**
- [x] **Step 4: Implement Anti-Ban Humanizer controls: Random Delay Interval slider (25s - 65s), Typing Simulation toggle, Auto-Rotate Engines toggle, and Batch Cooldown steppers (5 min / 25 msgs)**
- [x] **Step 5: Implement Default AI Rewrite Tone selector (Casual, Friendly, Formal)**

### Task 1.8: Phase 1 Verification & End-to-End Build Test
**Files:**
- Modify/Verify: All Phase 1 files
- Test: Integration tests for tab navigation, language switching, and UI rendering

- [x] **Step 1: Execute full Gradle compilation and Android test suite**
- [x] **Step 2: Verify all 4 tabs transition smoothly with spring animations**
- [x] **Step 3: Verify dynamic language switching between Indonesian and Turkish across all screens**
- [x] **Step 4: Phase 1 delivered and verified**

---

## Phase 2: Sinkronisasi Kontak Telepon ke Basis Data SQLite Lokal dengan Fitur Custom Nickname dan Filter Tag Kategori

### Task 2.1: Local Room Database & Data Access Layer
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/data/local/db/AppDatabase.kt`
- Create: `app/src/main/java/com/wabulk/sender/data/local/db/ContactEntity.kt`
- Create: `app/src/main/java/com/wabulk/sender/data/local/db/TagEntity.kt`
- Create: `app/src/main/java/com/wabulk/sender/data/local/db/ContactDao.kt`
- Create: `app/src/main/java/com/wabulk/sender/data/local/db/TagDao.kt`
- Test: `app/src/androidTest/java/com/wabulk/sender/data/ContactDaoTest.kt`

**Interfaces:**
- Produces: `ContactDao` with CRUD operations, search queries, tag filtering, and selection toggles.

- [x] **Step 1: Write Room DAO unit tests with in-memory database**
- [x] **Step 2: Implement `ContactEntity` with fields: `id`, `systemLookupKey`, `rawName`, `phoneNumber`, `customNickname`, `tagsJson`, `isSelected`**
- [x] **Step 3: Implement `ContactDao` with Flow queries for reactive UI updates**
- [x] **Step 4: Verify database operations pass all tests**

### Task 2.2: Non-Destructive Phonebook Contact Sync Engine
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/data/repository/ContactRepository.kt`
- Create: `app/src/main/java/com/wabulk/sender/data/sync/ContactSyncEngine.kt`
- Test: `app/src/test/java/com/wabulk/sender/ContactSyncEngineTest.kt`

**Interfaces:**
- Consumes: Android `ContactsContract.CommonDataKinds.Phone`.
- Produces: Non-destructive phonebook sync preserving user's custom nicknames and tags.

- [x] **Step 1: Write unit tests verifying deduplication and preservation of existing custom nicknames**
- [x] **Step 2: Implement `ContactSyncEngine` querying `ContactsContract` and upserting into Room**
- [x] **Step 3: Extract smart initial nickname fallback (e.g. first word of raw name, stripping parentheses/notes)**
- [x] **Step 4: Verify sync engine passes tests**

### Task 2.3: Contact Manager State Management & Reactive UI Integration
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/contacts/ContactViewModel.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/contacts/ContactManagerScreen.kt`

**Interfaces:**
- Consumes: `ContactRepository`.
- Produces: Live contacts state, debounced inline nickname updates, tag filtering, "Select All in Tag" actions.

- [x] **Step 1: Implement `ContactViewModel` with search query, active tag filter, and selected contact count**
- [x] **Step 2: Connect inline nickname text field with debounced repository update**
- [x] **Step 3: Implement quick format buttons ("First Name only", "Remove lead notes", "+ Hey / Hello")**
- [x] **Step 4: Connect "Select All in [Active Tag]" to batch toggle selection in Room**
- [x] **Step 5: Verify contact management features end-to-end**

---

## Phase 3: Editor Multi-Bubble, Lampiran Media, dan Integrasi API Cloudflare Worker untuk Groq/Gemini

### Task 3.1: Multi-Bubble Split Message Data Model & Editor
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/domain/model/MessageBubble.kt`
- Create: `app/src/main/java/com/wabulk/sender/domain/model/ComposerState.kt`
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/composer/ComposerViewModel.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/composer/ComposerScreen.kt`

**Interfaces:**
- Produces: Multi-bubble state (`List<MessageBubble>`), bubble reordering, adding/removing bubbles, inserting `{Nickname}`.

- [ ] **Step 1: Write tests for multi-bubble model and variable validation**
- [ ] **Step 2: Implement `ComposerViewModel` supporting multiple consecutive bubbles**
- [ ] **Step 3: Implement dynamic bubble list UI with "+ Add Bubble" button and per-bubble textarea**
- [ ] **Step 4: Connect variable pills (`+{Nickname}`, `+{Company}`) to insert text at cursor position**

### Task 3.2: Media Attachment Picker & Storage Resolver
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/data/media/MediaAttachmentManager.kt`
- Create: `app/src/main/java/com/wabulk/sender/domain/model/MediaAttachment.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/composer/components/MediaCarousel.kt`

**Interfaces:**
- Consumes: Android `ActivityResultContracts.GetMultipleContents`.
- Produces: Media preview, file size calculation, auto-compression metadata.

- [ ] **Step 1: Implement photo, video, and document file selection handler**
- [ ] **Step 2: Implement media thumbnail loader with badge and remove button**
- [ ] **Step 3: Integrate with WhatsApp bubble live preview**

### Task 3.3: Cloudflare Worker AI Bridge (Groq/Gemini)
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/data/remote/CloudflareAiApi.kt`
- Create: `app/src/main/java/com/wabulk/sender/data/remote/dto/AiRewriteRequest.kt`
- Create: `app/src/main/java/com/wabulk/sender/data/remote/dto/AiRewriteResponse.kt`
- Create: `app/src/main/java/com/wabulk/sender/data/repository/AiRewriteRepository.kt`
- Test: `app/src/test/java/com/wabulk/sender/AiRewriteRepositoryTest.kt`

**Interfaces:**
- Consumes: User message draft, recipient category context, language (`ID` / `TR`), AI mode (single bubble spintax vs. 2-3 split bubbles).
- Produces: Rewritten bubbles with `{Nickname}` token guaranteed intact.

- [ ] **Step 1: Write unit tests verifying token locking mechanism for `{Nickname}`**
- [ ] **Step 2: Implement OkHttp & Retrofit interface for Cloudflare Worker**
- [ ] **Step 3: Implement token protection guardrail (validates that response preserves `{Nickname}`)**
- [ ] **Step 4: Connect AI Rewrite button in Composer UI with loading state and tone selector**

---

## Phase 4: Layanan Foreground Android dengan Mesin WhatsApp Linked Device Ganda via 8-Digit Pairing Code

### Task 4.1: Android Foreground Service & Lifecycle Manager
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/service/WhatsAppForegroundService.kt`
- Create: `app/src/main/java/com/wabulk/sender/service/NotificationHelper.kt`
- Modify: `app/src/main/AndroidManifest.xml`

**Interfaces:**
- Produces: Persistent background service with ongoing status notification, preventing Android OS battery killer terminations.

- [ ] **Step 1: Configure AndroidManifest with `FOREGROUND_SERVICE` and WakeLock permissions**
- [ ] **Step 2: Implement `NotificationHelper` creating notification channels and live status updates**
- [ ] **Step 3: Implement `WhatsAppForegroundService` with lifecycle management and sticky restart**

### Task 4.2: Dual-Node WhatsApp Engine & 8-Digit Pairing Code Implementation
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/engine/WhatsAppEngine.kt`
- Create: `app/src/main/java/com/wabulk/sender/engine/WhatsAppSessionManager.kt`
- Create: `app/src/main/java/com/wabulk/sender/engine/model/NodeSession.kt`
- Create: `app/src/main/java/com/wabulk/sender/engine/model/NodeStatus.kt`
- Test: `app/src/test/java/com/wabulk/sender/WhatsAppSessionTest.kt`

**Interfaces:**
- Consumes: Phone number input, pairing code requests.
- Produces: 8-digit pairing code string (e.g. `K97B-42VX`), countdown expiration timer, session encryption, connection state flow.

- [ ] **Step 1: Write tests for session state transitions and 8-digit code formatting**
- [ ] **Step 2: Implement `WhatsAppEngine` protocol handler managing Node 1 (Personal) and Node 2 (Business)**
- [ ] **Step 3: Implement pairing code generation flow with 3-minute timeout and automatic retry**
- [ ] **Step 4: Integrate "Copy Code & Open WhatsApp" deep link intent to launch WhatsApp on the same phone**
- [ ] **Step 5: Verify dual-node session connectivity and keepalive monitoring**

---

## Phase 5: Mesin Antrean Pengiriman Dua Lapis Jeda dan Pengaman Anti-Ban

### Task 5.1: Two-Tier Rate-Limiting Jitter & Dispatch Engine
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/engine/queue/DispatchQueueManager.kt`
- Create: `app/src/main/java/com/wabulk/sender/engine/queue/JitterCalculator.kt`
- Create: `app/src/main/java/com/wabulk/sender/engine/queue/model/QueueTask.kt`
- Test: `app/src/test/java/com/wabulk/sender/JitterCalculatorTest.kt`
- Test: `app/src/test/java/com/wabulk/sender/DispatchQueueManagerTest.kt`

**Interfaces:**
- Consumes: Selected contacts, composed bubbles, safety settings (25-60s delay, typing toggle, batch cooldown).
- Produces: Automated execution pipeline with two-tier delays and round-robin node alternation.

- [ ] **Step 1: Write unit tests verifying Gaussian jitter distribution and tier-1/tier-2 delays**
- [ ] **Step 2: Implement `JitterCalculator` with organic randomization**
- [ ] **Step 3: Implement `DispatchQueueManager` orchestrating:**
  - Round-robin selection between Node 1 and Node 2 when "Dual Sync" is active
  - Tier 1: 3-6s typing status between bubbles for the same contact
  - Tier 2: 25-60s jitter delay between different contacts
  - Batch rest cycle: 5-minute dormancy every 25 messages
- [ ] **Step 4: Verify queue logic with comprehensive automated tests**

### Task 5.2: Live Queue StateFlow Telemetry & Tactical Control Integration
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/queue/QueueViewModel.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/queue/LiveQueueScreen.kt`
- Modify: `app/src/main/java/com/wabulk/sender/service/WhatsAppForegroundService.kt`

**Interfaces:**
- Consumes: `DispatchQueueManager` state flows.
- Produces: Real-time progress bar updates, countdown timers, live typing bouncing dots, Pause & Emergency Stop buttons.

- [ ] **Step 1: Bind `QueueViewModel` to `DispatchQueueManager` state flows**
- [ ] **Step 2: Connect Pause Queue button to immediately pause processing and freeze timers**
- [ ] **Step 3: Connect Emergency Stop Blast button to cancel all remaining pending tasks and reset state**
- [ ] **Step 4: Update Android Foreground Service notification with live progress (e.g. "Sending 18/42 (43%) - Next in 34s")**

### Task 5.3: End-to-End System Verification & Regression Testing
**Files:**
- Test: Full integration test suite

- [ ] **Step 1: Run comprehensive unit and instrumentation test suite**
- [ ] **Step 2: Verify queue execution under simulated network disconnects and pause/resume**
- [ ] **Step 3: Verify zero leaks and clean service termination on completion**
- [ ] **Step 4: Final documentation update and readiness review**
