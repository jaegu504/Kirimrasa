# Kirimrasa (WhatsApp Bulk Sender)

Android app (Kotlin, Jetpack Compose, Material 3, Room) that sends personal-feeling bulk WhatsApp messages to the user's own contacts. Package `com.wabulk.sender`. The user writes in Bahasa Indonesia.

## Build

Needs JDK 17 (`jvmToolchain(17)`) and the Android SDK at `~/Library/Android/sdk`. compileSdk/targetSdk 36, minSdk 26. `java_home` does not find the installed JDK; set it explicitly:
`export JAVA_HOME=~/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home`

The gateway API key is baked in from `local.properties` (`gateway.apiKey=...`, not in git) and must match `API_KEY` on Render. The gateway refuses to start in production without `API_KEY`.

```bash
./gradlew assembleDebug
./gradlew test                 # unit tests in app/src/test
./gradlew assembleRelease      # R8 minify + shrink; keep rules in app/proguard-rules.pro
```

- Release builds are still signed with the debug key (`signingConfig = debug`). Changing the key breaks in-app updates for existing installs.
- New release: bump `versionCode`/`versionName` in `app/build.gradle.kts`, tag `vX.Y.Z`, publish the APK on GitHub Releases (`jaegu504/Kirimrasa`); `update/` reads it.
- Classes loaded by reflection or serialization (Room, kotlinx.serialization DTOs, update manager) need proguard keep rules.

## Structure

`app/src/main/java/com/wabulk/sender/`
- `data/` – Room DB (`local/db`), phonebook sync (`sync`), repositories, CSV import/export (`io`), media, AI client (`remote`), ID/TR strings (`locale`)
- `domain/` – models (message bubbles, media), spintax parser
- `engine/` – `WhatsAppEngine` implementations, Baileys bridge client (`baileys`), dispatch queue and jitter (`queue`)
- `service/` – foreground service that keeps sending alive
- `ui/` – 4 tabs: composer, contacts, queue, settings; plus tutorial and update UI
- `theme/` – colors, type (Plus Jakarta Sans), springs, neumorphism. Use these tokens; don't hardcode colors
- `update/` – in-app auto-update from GitHub Releases

Outside the app:
- `wa-gateway/` – Node Baileys gateway (Render: `kirimrasa-gateway.onrender.com`)
- `cloudflare-worker/worker.js` – AI proxy to Groq/Gemini (`kirimrasa.jaegu108.workers.dev`)
- `plan.md`, `docs/superpowers/plans/` – original phase plans. Their checklists are stale: all phases are implemented; trust code and `git log`.

## Rules

- Never write to the Android phonebook (`ContactsContract`). Nicknames, tags and CSV-imported contacts live only in Room. Sync must merge without duplicates and keep user nicknames/tags.
- `{Nickname}`, `{Company}`, `{Custom_1}` and spintax `{A|B}` must survive AI rewrites and language switches verbatim. Validate AI output before accepting it.
- Keep the two-tier anti-ban delays: 3–6 s typing between bubbles to one contact, 25–60 s random jitter between contacts, batch cooldown (e.g. 5 min every 25 messages). Don't shorten them for speed.
- Pause/Stop must halt dispatch immediately and cancel pending delays.
- Every user-visible string goes in both Indonesian and Turkish (`data/locale`). Language switches at runtime without restart or losing drafts.
- Room schema changes need a migration that keeps existing contacts, nicknames and tags.
- No paid WhatsApp API. Sending goes through the Baileys gateway via an 8-digit pairing code.
- Every clickable control must do something; no empty `{ }` handlers.
