# Phase 3 Implementation Plan: Multi-Bubble Editor, Media Attachments & Cloudflare AI Bridge

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the multi-bubble split message editor, media attachment manager with thumbnail carousel, and Cloudflare Worker AI proxy integration (Groq LPU + Gemini fallback) with strict `{Nickname}` token preservation guardrails.

**Architecture:** Clean MVVM Architecture. Domain models (`MessageBubble`, `MediaAttachment`) encapsulate message structure and spintax parsing. Data layer (`AiRewriteRepository`, `CloudflareAiApi`) communicates with the Cloudflare Worker via OkHttp/Retrofit and enforces strict variable locking guardrails. Compose UI (`ComposerViewModel`, `ComposerScreen`) reactively mirrors user edits into real-time WhatsApp preview bubbles.

**Tech Stack:** Kotlin 2.3+, Jetpack Compose (Material 3), OkHttp 4 / Retrofit 2, Kotlinx Serialization, Coil Compose, JUnit 4, Kotlinx Coroutines Test.

**Spec:** `plan.md` (Phase 3) and `2026-10-17-wa-bulk-sender-design copy.md`.

---

## Global Constraints

- **Variable Protection:** The `{Nickname}` token and spintax syntax `{A|B}` must never be altered, translated, or removed during AI rewrites.
- **Multi-Bubble Split:** Allow 1 to 3 bubbles per campaign broadcast.
- **Design Tokens:** Strict adherence to Emerald Cybernetic color palette (`#0F1419`, `#171C21`, `#25D366`, `#4EDE93`).
- **Dynamic Config:** Cloudflare Worker endpoint URL must be configurable at runtime.
- **Test-Driven:** 100% test coverage for spintax parsing, variable guardrails, and multi-bubble state transitions.

---

## Review Focus

1. **Token Preservation:** Input containing `{Nickname}` must return bubbles where `{Nickname}` is present verbatim.
2. **Spintax Execution:** `{Hai|Halo}` resolves into either "Hai" or "Halo" without breaking surrounding punctuation.
3. **Empty Bubble Handling:** Adding, deleting, and reordering bubbles does not crash if text is empty or bubbles are deleted down to 1.
4. **Offline Resilience:** If Cloudflare Worker is unreachable, AI rewrite fails gracefully without crashing or losing the user's draft.
5. **WhatsApp Live Preview:** All bubbles and media thumbnails render reactively in the preview card with double blue ticks.

---

### Task 3.1: Multi-Bubble Data Models & Spintax Processor
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/domain/model/MessageBubble.kt`
- Create: `app/src/main/java/com/wabulk/sender/domain/model/ComposerState.kt`
- Create: `app/src/main/java/com/wabulk/sender/domain/spintax/SpintaxParser.kt`
- Test: `app/src/test/java/com/wabulk/sender/SpintaxParserTest.kt`
- Test: `app/src/test/java/com/wabulk/sender/MessageBubbleTest.kt`

- [ ] **Step 1:** Write unit tests for `SpintaxParser` (nested spin, random choice, token isolation).
- [ ] **Step 2:** Write unit tests for `MessageBubble` management (add, remove, reorder, validate).
- [ ] **Step 3:** Implement `SpintaxParser` with regex-based spin resolver and variable protector.
- [ ] **Step 4:** Implement `MessageBubble` and `ComposerState` data classes.
- [ ] **Step 5:** Run tests and verify all PASS.

---

### Task 3.2: Media Attachment Resolver & Carousel Model
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/domain/model/MediaAttachment.kt`
- Create: `app/src/main/java/com/wabulk/sender/data/media/MediaAttachmentManager.kt`
- Test: `app/src/test/java/com/wabulk/sender/MediaAttachmentTest.kt`

- [ ] **Step 1:** Write unit tests for `MediaAttachment` (file size formatting, mime-type classification).
- [ ] **Step 2:** Implement `MediaAttachment` data model with helper extensions.
- [ ] **Step 3:** Implement `MediaAttachmentManager` handling addition, deletion, and maximum capacity (max 5 items).
- [ ] **Step 4:** Run tests and verify all PASS.

---

### Task 3.3: Cloudflare Worker AI Bridge & Guardrail Token Locking
**Files:**
- Modify: `gradle/libs.versions.toml` & `app/build.gradle.kts` (Add OkHttp/Retrofit/Coil)
- Create: `app/src/main/java/com/wabulk/sender/data/remote/dto/AiRewriteRequest.kt`
- Create: `app/src/main/java/com/wabulk/sender/data/remote/dto/AiRewriteResponse.kt`
- Create: `app/src/main/java/com/wabulk/sender/data/remote/CloudflareAiApi.kt`
- Create: `app/src/main/java/com/wabulk/sender/data/repository/AiRewriteRepository.kt`
- Test: `app/src/test/java/com/wabulk/sender/AiRewriteRepositoryTest.kt`

- [ ] **Step 1:** Write unit tests for `AiRewriteRepository` testing token preservation guardrail (restores `{Nickname}` if missing in LLM response).
- [ ] **Step 2:** Configure Gradle dependencies for networking and serialization.
- [ ] **Step 3:** Implement request/response DTOs matching `cloudflare-worker/worker.js`.
- [ ] **Step 4:** Implement `CloudflareAiApi` and `AiRewriteRepository` with guardrail validator.
- [ ] **Step 5:** Run tests and verify all PASS.

---

### Task 3.4: Composer ViewModel & Reactive UI Wiring
**Files:**
- Create: `app/src/main/java/com/wabulk/sender/ui/screens/composer/ComposerViewModel.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/composer/ComposerScreen.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/composer/components/WhatsAppPreviewBubble.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/screens/composer/components/MediaCarousel.kt`
- Modify: `app/src/main/java/com/wabulk/sender/ui/MainScreen.kt`
- Test: `app/src/test/java/com/wabulk/sender/ComposerViewModelTest.kt`

- [ ] **Step 1:** Write unit tests for `ComposerViewModel` (bubble addition, variable insertion, AI rewrite state transitions).
- [ ] **Step 2:** Implement `ComposerViewModel` managing multi-bubble list, account node selection, and media attachments.
- [ ] **Step 3:** Update `ComposerScreen` with multi-bubble editor, dynamic "+ Balon Pesan", and AI rewrite button with loading indicator.
- [ ] **Step 4:** Update `WhatsAppPreviewBubble` to render consecutive message bubbles in WhatsApp green style.
- [ ] **Step 5:** Wire `MainScreen` to inject `ComposerViewModel`.
- [ ] **Step 6:** Run full test suite, install APK on emulator, and take visual verification screenshots.
