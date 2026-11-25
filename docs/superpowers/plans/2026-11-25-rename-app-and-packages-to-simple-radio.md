# Rename App Name and Packages to Simple Radio Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rename Android app display name from "Jetpack" to "Simple Radio" and migrate the base package name and namespaces from `com.alxnophis.jetpack` to `com.alxnophis.simpleradio` across all modules.

**Architecture:** A multi-module Android project where Gradle namespaces, application ID, Proguard keep rules, Kover filters, Fastlane config, string resources, and Kotlin source directory structures/package statements are refactored to use `com.alxnophis.simpleradio`.

**Tech Stack:** Kotlin 2.4.20, Android Gradle Plugin, Jetpack Compose, Koin, Roborazzi, JUnit 5.

**Spec:** User request: "rename the jetpack app name and packages to 'simple radio'" with accepted defaults: display name "Simple Radio", application ID `com.alxnophis.simpleradio`, package name `com.alxnophis.simpleradio`, and `JetpackApp` renamed to `SimpleRadioApp`.

## Global Constraints

- Never commit changes without user review and explicit order (AGENTS.md: "DO NOT commit without review").
- Base package name across all 12 modules must be `com.alxnophis.simpleradio.<module>` (and `com.alxnophis.simpleradio` for `:app`).
- App display name in `app/src/main/res/values/strings.xml` must be "Simple Radio".
- Code formatting must adhere to KtLint (`./gradlew ktlintCheck`).
- All unit tests must pass (`./gradlew testDebugUnitTest`).

## Review Focus

1. Unmigrated BuildConfig or R class references: verify imports referencing `.R` or `.BuildConfig` in all modules use `com.alxnophis.simpleradio.<module>.R` / `.BuildConfig`.
2. Proguard keep rules: verify `shared/api/proguard-rules.pro` and `shared/storage/proguard-rules.pro` match the new package name.
3. Kover multi-module coverage filter: verify `buildSystem/gradle/kover-merge.gradle` includes/excludes match `com.alxnophis.simpleradio.*`.
4. Fastlane configuration: verify `fastlane/Appfile` references `com.alxnophis.simpleradio`.
5. Missing directory migrations: verify zero remaining source directories under `com/alxnophis/jetpack`.

---

### Task 1: Update App Display Name and Application Class

**Files:**
- Modify: `app/src/main/res/values/strings.xml`
- Move & Modify: `app/src/main/java/com/alxnophis/jetpack/application/JetpackApp.kt` -> `SimpleRadioApp.kt`
- Modify: `app/src/main/AndroidManifest.xml`

**Interfaces:**
- Produces: `com.alxnophis.simpleradio.application.SimpleRadioApp` extending `BaseApp`

- [ ] **Step 1: Update app display name in `app/src/main/res/values/strings.xml`**
  Change `<string name="app_name">Jetpack</string>` to `<string name="app_name">Simple Radio</string>`.

- [ ] **Step 2: Rename `JetpackApp` to `SimpleRadioApp` in `app/src/main/AndroidManifest.xml`**
  Update `android:name=".application.JetpackApp"` to `android:name=".application.SimpleRadioApp"`.

- [ ] **Step 3: Rename `JetpackApp.kt` to `SimpleRadioApp.kt` and update class definition**
  Rename file and update class name from `JetpackApp` to `SimpleRadioApp`.

---

### Task 2: Update Build Configuration, Application ID, Namespaces, Proguard, and Tools

**Files:**
- Modify: `buildSrc/src/main/java/AppConfig.kt`
- Modify: `buildSystem/gradle/common-app-base.gradle`
- Modify: `buildSystem/gradle/kover-merge.gradle`
- Modify: `fastlane/Appfile`
- Modify: `shared/api/proguard-rules.pro`
- Modify: `shared/storage/proguard-rules.pro`
- Modify: `app/build.gradle`
- Modify: `feature/root/build.gradle`
- Modify: `feature/home/build.gradle`
- Modify: `feature/search/build.gradle`
- Modify: `feature/favorites/build.gradle`
- Modify: `feature/settings/build.gradle`
- Modify: `feature/radio-player/build.gradle`
- Modify: `shared/core/build.gradle`
- Modify: `shared/api/build.gradle`
- Modify: `shared/kotlin/build.gradle`
- Modify: `shared/storage/build.gradle`
- Modify: `shared/testing/build.gradle`

**Interfaces:**
- Produces: `AppConfig.APPLICATION_ID = "com.alxnophis.simpleradio"`
- Produces: updated namespaces for all 12 modules under `com.alxnophis.simpleradio.*`

- [ ] **Step 1: Update `AppConfig.kt`**
  Change `APPLICATION_ID = "com.alxnophis.jetpack"` to `"com.alxnophis.simpleradio"`.

- [ ] **Step 2: Update APK naming and Kover in `buildSystem/gradle/`**
  In `buildSystem/gradle/common-app-base.gradle`, update `projectName = "jetpack"` to `projectName = "simpleradio"`.
  In `buildSystem/gradle/kover-merge.gradle`, replace all `com.alxnophis.jetpack` with `com.alxnophis.simpleradio`.

- [ ] **Step 3: Update `fastlane/Appfile` and Proguard rules**
  In `fastlane/Appfile`, change `package_name("com.alxnophis.jetpack")` to `package_name("com.alxnophis.simpleradio")`.
  In `shared/api/proguard-rules.pro`, change `-keep class com.alxnophis.jetpack.api.**` to `-keep class com.alxnophis.simpleradio.api.**`.
  In `shared/storage/proguard-rules.pro`, change `-keep class com.alxnophis.jetpack.storage.**` to `-keep class com.alxnophis.simpleradio.storage.**`.

- [ ] **Step 4: Update `namespace` across all 12 modules' `build.gradle`**
  - `app/build.gradle`: `com.alxnophis.simpleradio`
  - `feature/root/build.gradle`: `com.alxnophis.simpleradio.root`
  - `feature/home/build.gradle`: `com.alxnophis.simpleradio.home`
  - `feature/search/build.gradle`: `com.alxnophis.simpleradio.search`
  - `feature/favorites/build.gradle`: `com.alxnophis.simpleradio.favorites`
  - `feature/settings/build.gradle`: `com.alxnophis.simpleradio.settings`
  - `feature/radio-player/build.gradle`: `com.alxnophis.simpleradio.radioplayer`
  - `shared/core/build.gradle`: `com.alxnophis.simpleradio.core`
  - `shared/api/build.gradle`: `com.alxnophis.simpleradio.api`
  - `shared/kotlin/build.gradle`: `com.alxnophis.simpleradio.kotlin`
  - `shared/storage/build.gradle`: `com.alxnophis.simpleradio.storage`
  - `shared/testing/build.gradle`: `com.alxnophis.simpleradio.testing`

---

### Task 3: Move Source Tree Directories

**Files:**
- Move: all 20 source directories from `com/alxnophis/jetpack` to `com/alxnophis/simpleradio` across `app`, `feature/*`, and `shared/*`.

**Interfaces:**
- Produces: directory trees rooted at `com/alxnophis/simpleradio` for all Kotlin/Java source sets.

- [ ] **Step 1: Execute `git mv` on all 20 package directories**
  Move each `com/alxnophis/jetpack` directory to `com/alxnophis/simpleradio`:
  - `app/src/main/java/com/alxnophis/jetpack` -> `app/src/main/java/com/alxnophis/simpleradio`
  - `shared/core/src/main/java/com/alxnophis/jetpack` -> `shared/core/src/main/java/com/alxnophis/simpleradio`
  - `shared/core/src/test/java/com/alxnophis/jetpack` -> `shared/core/src/test/java/com/alxnophis/simpleradio`
  - `shared/kotlin/src/main/java/com/alxnophis/jetpack` -> `shared/kotlin/src/main/java/com/alxnophis/simpleradio`
  - `shared/storage/src/main/java/com/alxnophis/jetpack` -> `shared/storage/src/main/java/com/alxnophis/simpleradio`
  - `shared/storage/src/test/java/com/alxnophis/jetpack` -> `shared/storage/src/test/java/com/alxnophis/simpleradio`
  - `shared/testing/src/main/java/com/alxnophis/jetpack` -> `shared/testing/src/main/java/com/alxnophis/simpleradio`
  - `shared/api/src/main/java/com/alxnophis/jetpack` -> `shared/api/src/main/java/com/alxnophis/simpleradio`
  - `feature/settings/src/main/java/com/alxnophis/jetpack` -> `feature/settings/src/main/java/com/alxnophis/simpleradio`
  - `feature/settings/src/test/java/com/alxnophis/jetpack` -> `feature/settings/src/test/java/com/alxnophis/simpleradio`
  - `feature/home/src/main/java/com/alxnophis/jetpack` -> `feature/home/src/main/java/com/alxnophis/simpleradio`
  - `feature/home/src/test/java/com/alxnophis/jetpack` -> `feature/home/src/test/java/com/alxnophis/simpleradio`
  - `feature/favorites/src/main/java/com/alxnophis/jetpack` -> `feature/favorites/src/main/java/com/alxnophis/simpleradio`
  - `feature/favorites/src/test/java/com/alxnophis/jetpack` -> `feature/favorites/src/test/java/com/alxnophis/simpleradio`
  - `feature/radio-player/src/main/java/com/alxnophis/jetpack` -> `feature/radio-player/src/main/java/com/alxnophis/simpleradio`
  - `feature/radio-player/src/test/kotlin/com/alxnophis/jetpack` -> `feature/radio-player/src/test/kotlin/com/alxnophis/simpleradio`
  - `feature/search/src/main/java/com/alxnophis/jetpack` -> `feature/search/src/main/java/com/alxnophis/simpleradio`
  - `feature/search/src/test/java/com/alxnophis/jetpack` -> `feature/search/src/test/java/com/alxnophis/simpleradio`
  - `feature/root/src/main/java/com/alxnophis/jetpack` -> `feature/root/src/main/java/com/alxnophis/simpleradio`
  - `feature/root/src/test/java/com/alxnophis/jetpack` -> `feature/root/src/test/java/com/alxnophis/simpleradio`

- [ ] **Step 2: Verify no `jetpack` directory remains in source sets**
  Run: `find . -type d -name "jetpack" | grep -v "/build/"`
  Expected: empty result.

---

### Task 4: Refactor Package Names and Imports in Kotlin Sources and Documentation

**Files:**
- Modify: All `*.kt` files under `app/`, `feature/*/`, and `shared/*/`
- Modify: `AGENTS.md`
- Modify: `docs/SETUP.md`
- Modify: `docs/E2E_TESTING.md`
- Modify: `docs/adr/0002-end-to-end-testing-strategy.md`

**Interfaces:**
- Produces: All Kotlin source files compiling under `com.alxnophis.simpleradio.*`

- [ ] **Step 1: Replace package and import statements across Kotlin files**
  In all `*.kt` files, replace:
  - `package com.alxnophis.jetpack` with `package com.alxnophis.simpleradio`
  - `import com.alxnophis.jetpack.` with `import com.alxnophis.simpleradio.`
  - KDoc references `com.alxnophis.jetpack.` with `com.alxnophis.simpleradio.`

- [ ] **Step 2: Update documentation files**
  In `AGENTS.md`, update references from `com.alxnophis.jetpack.*` to `com.alxnophis.simpleradio.*`.
  In `docs/SETUP.md`, `docs/E2E_TESTING.md`, and `docs/adr/0002-end-to-end-testing-strategy.md`, replace `com.alxnophis.jetpack` with `com.alxnophis.simpleradio`.

- [ ] **Step 3: Verify zero occurrences of `com.alxnophis.jetpack` remaining**
  Run: `git grep "com\.alxnophis\.jetpack"`
  Expected: zero matches.

---

### Task 5: Formatting and Compilation Verification

**Files:**
- Verification only

- [ ] **Step 1: Run ktlint formatting check**
  Run: `./gradlew ktlintCheck`
  Expected: BUILD SUCCESSFUL (if formatting adjustments needed, run `./gradlew ktlintFormat` then re-verify).

- [ ] **Step 2: Run all unit tests**
  Run: `./gradlew testDebugUnitTest`
  Expected: BUILD SUCCESSFUL with all tests passing.

- [ ] **Step 3: Run Android Lint check**
  Run: `./gradlew lintDebug`
  Expected: BUILD SUCCESSFUL.
