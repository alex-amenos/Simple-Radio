# Persistent Mini Player Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a persistent mini player bar showing the current station artwork, title, and play/pause controls docked directly above the `NavigationBar`, with tap-to-expand to the full player.

**Architecture:** Maintain strict module decoupling: `:feature:home` exposes a `@Composable () -> Unit` slot above `NavigationBar`. `:feature:radio-player` exposes `RadioPlayerBarFeature` driven by a new `RadioPlayerBarViewModel` that shares the singleton `RadioPlayer`. `:feature:root` wires the two together in `Navigation.kt`.

**Tech Stack:** Kotlin 2.4, Jetpack Compose Material 3, Koin DI, Coil (`AsyncImage`), Arrow, Roborazzi, JUnit 5, Turbine.

**Spec:** `docs/superpowers/specs/2026-11-25-persistent-mini-player-design.md`

## Global Constraints
- Feature modules must not depend on other feature modules (`:feature:home` must not depend on `:feature:radio-player`).
- All ViewModels, UI components, and internal classes must be `internal`; only feature entry points (`RadioPlayerBarFeature`) are `public`.
- No wildcard imports, alphabetized import grouping.
- Conventional Commits specification for all commit messages.

## Review Focus
- Input: Station name is extremely long -> Text must truncate with `TextOverflow.Ellipsis` without pushing the play/pause button off screen.
- Input: Artwork URI is null or invalid -> Fallback radio icon vector must render cleanly in the square thumbnail box.
- Input: Rapid toggling between play and pause -> UI state must react immediately without desynchronizing from `RadioPlayer`.
- Input: Transition from Idle to Playing -> Mini player must appear without jank or obscuring `NavigationBar` content.
- Input: Navigation back from full player to Home -> Mini player remains visible and up-to-date with current playback status.

---

### Task 1: Add mini player slot support to `:feature:home`

**Files:**
- Modify: `feature/home/src/main/java/com/alxnophis/simpleradio/home/ui/composable/HomeScreen.kt`
- Modify: `feature/home/src/main/java/com/alxnophis/simpleradio/home/ui/composable/HomeFeature.kt`
- Modify: `feature/home/src/main/java/com/alxnophis/simpleradio/home/ui/composable/HomeTags.kt`
- Create: `feature/home/src/test/java/com/alxnophis/simpleradio/home/ui/composable/HomeScreenTest.kt`

**Interfaces:**
- Consumes: Nothing
- Produces: `HomeFeature(..., miniPlayerContent: @Composable () -> Unit = {})` and `HomeScreen(..., miniPlayerContent: @Composable () -> Unit = {})`

- [ ] **Step 1: Write the failing test for `HomeScreen` with `miniPlayerContent`**

In `feature/home/src/test/java/com/alxnophis/simpleradio/home/ui/composable/HomeScreenTest.kt`:
Test that `HomeScreen` renders `miniPlayerContent` inside `bottomBar` above the navigation bar.

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :feature:home:testDebugUnitTest --tests "*.HomeScreenTest"`
Expected: FAIL (compilation error: `miniPlayerContent` parameter not yet added)

- [ ] **Step 3: Implement `miniPlayerContent` slot in `HomeScreen.kt` and `HomeFeature.kt`**

Add `miniPlayerContent: @Composable () -> Unit = {}` to `HomeScreen` and wrap in `Column { miniPlayerContent(); NavigationBar(...) }`. Pass parameter through `HomeFeature`.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :feature:home:testDebugUnitTest`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add feature/home/
git commit -m "feat(home): add mini player content slot above navigation bar"
```

---

### Task 2: Define Contract and ViewModel for Mini Player Bar in `:feature:radio-player`

**Files:**
- Create: `feature/radio-player/src/main/java/com/alxnophis/simpleradio/radioplayer/ui/contract/RadioPlayerBarContract.kt`
- Create: `feature/radio-player/src/main/java/com/alxnophis/simpleradio/radioplayer/ui/viewmodel/RadioPlayerBarViewModel.kt`
- Modify: `feature/radio-player/src/main/java/com/alxnophis/simpleradio/radioplayer/di/RadioModule.kt`
- Create: `feature/radio-player/src/test/kotlin/com/alxnophis/simpleradio/radioplayer/ui/viewmodel/RadioPlayerBarViewModelTest.kt`

**Interfaces:**
- Consumes: `RadioPlayer.snapshot: StateFlow<RadioPlayerSnapshot>`, `RadioPlayer.play()`, `RadioPlayer.pause()`
- Produces: `RadioPlayerBarViewModel`, `RadioPlayerBarUiState(val isVisible: Boolean, val stationName: String?, val artworkUri: String?, val status: RadioPlaybackStatus)`, `RadioPlayerBarEvent` (Play, Pause)

- [ ] **Step 1: Write the failing test for `RadioPlayerBarViewModel`**

In `feature/radio-player/src/test/kotlin/com/alxnophis/simpleradio/radioplayer/ui/viewmodel/RadioPlayerBarViewModelTest.kt`:
Test that:
1. `uiState` is not visible when snapshot stationId is null and status is Idle.
2. `uiState` becomes visible when station is loaded and Playing or Paused.
3. `handleEvent(Play)` delegates to `player.play()`.
4. `handleEvent(Pause)` delegates to `player.pause()`.

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :feature:radio-player:testDebugUnitTest --tests "*.RadioPlayerBarViewModelTest"`
Expected: FAIL (unresolved reference: `RadioPlayerBarViewModel`)

- [ ] **Step 3: Implement `RadioPlayerBarContract`, `RadioPlayerBarViewModel`, and register in `RadioModule.kt`**

Implement state mapping with `stateIn` and event handling delegating to `RadioPlayer`.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :feature:radio-player:testDebugUnitTest --tests "*.RadioPlayerBarViewModelTest"`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add feature/radio-player/
git commit -m "feat(radio-player): add RadioPlayerBarContract and RadioPlayerBarViewModel"
```

---

### Task 3: Build the UI Composable `RadioPlayerBar` and `RadioPlayerBarFeature` in `:feature:radio-player`

**Files:**
- Create: `feature/radio-player/src/main/java/com/alxnophis/simpleradio/radioplayer/ui/composable/RadioPlayerBarTags.kt`
- Create: `feature/radio-player/src/main/java/com/alxnophis/simpleradio/radioplayer/ui/composable/RadioPlayerBar.kt`
- Create: `feature/radio-player/src/test/kotlin/com/alxnophis/simpleradio/radioplayer/ui/composable/RadioPlayerBarScreenshotTest.kt`

**Interfaces:**
- Consumes: `RadioPlayerBarViewModel`, `RadioPlayerBarUiState`, `RadioPlayerBarEvent`
- Produces: `public fun RadioPlayerBarFeature(onOpenPlayer: () -> Unit, modifier: Modifier = Modifier)`

- [ ] **Step 1: Write failing screenshot test for `RadioPlayerBar`**

Create `RadioPlayerBarScreenshotTest.kt` testing Playing, Paused, Connecting (loading spinner), and Dark theme states.

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :feature:radio-player:testDebugUnitTest --tests "*.RadioPlayerBarScreenshotTest"`
Expected: FAIL (unresolved reference: `RadioPlayerBar`)

- [ ] **Step 3: Implement `RadioPlayerBar` and `RadioPlayerBarFeature`**

Build `RadioPlayerBar`:
- Docked surface/card above NavigationBar.
- 40x40dp square artwork with rounded corners (using Coil's `AsyncImage` with fallback radio icon).
- Station title with single-line `TextOverflow.Ellipsis`.
- Play/Pause icon button (or CircularProgressIndicator when connecting).
- Clickable bar body invoking `onOpenPlayer()`.
- Test tags for Robolectric/UI testing.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :feature:radio-player:testDebugUnitTest --tests "*.RadioPlayerBarScreenshotTest"`
Run: `./gradlew recordRoborazziDebug` to record new baseline screenshots for RadioPlayerBar.
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add feature/radio-player/
git commit -m "feat(radio-player): create RadioPlayerBar and RadioPlayerBarFeature composables"
```

---

### Task 4: Wire `RadioPlayerBarFeature` into `Navigation.kt` in `:feature:root`

**Files:**
- Modify: `feature/root/src/main/java/com/alxnophis/simpleradio/root/ui/navigation/Navigation.kt`
- Modify: `feature/root/src/test/java/com/alxnophis/simpleradio/root/ui/viewmodel/RootViewModelUnitTests.kt` (or navigation test)

**Interfaces:**
- Consumes: `RadioPlayerBarFeature(onOpenPlayer: () -> Unit)` from `:feature:radio-player`, `HomeFeature(miniPlayerContent = ...)` from `:feature:home`
- Produces: Root navigation containing persistent mini player bar on Home screen

- [ ] **Step 1: Wire `RadioPlayerBarFeature` into `Navigation.kt`**

In `feature/root/src/main/java/com/alxnophis/simpleradio/root/ui/navigation/Navigation.kt`:
Pass `miniPlayerContent = { RadioPlayerBarFeature(onOpenPlayer = ::showRadioPlayer) }` into `HomeFeature`.

- [ ] **Step 2: Run root module tests to verify integration**

Run: `./gradlew :feature:root:testDebugUnitTest`
Expected: PASS

- [ ] **Step 3: Commit**

```bash
git add feature/root/
git commit -m "feat(root): wire RadioPlayerBarFeature into HomeFeature navigation"
```

---

### Task 5: End-to-end CI validation and formatting

**Files:**
- All modified/created files

- [ ] **Step 1: Run ktlintCheck and ktlintFormat**

Run: `./gradlew ktlintCheck`
(If any formatting issues: `./gradlew ktlintFormat`)

- [ ] **Step 2: Run Android Lint**

Run: `./gradlew lintDebug`
Expected: PASS (no lint errors)

- [ ] **Step 3: Run all unit tests and verify Roborazzi screenshot tests**

Run: `./gradlew verifyRoborazziDebug`
Run: `./gradlew koverHtmlReportDebug`
Expected: PASS (all tests pass)

- [ ] **Step 4: Commit any formatting or lint fixes**

```bash
git commit -am "chore: code formatting and lint fixes"
```
