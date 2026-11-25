# Persistent Mini Player Design Specification

## 1. Intent & Purpose

Users browsing radio stations across tabs (Favorites, Search, Settings) currently have no persistent playback controls once they navigate away from the full Radio Player screen. To control playback or view the currently playing station, they must either return to the full player or rely on system notifications.

This feature introduces a persistent mini player bar docked directly above the bottom `NavigationBar`, displaying the current station artwork/icon, station title, and quick play/pause controls, with immediate navigation to the full player upon tap.

---

## 2. Goals, Non-Goals, and Anti-Goals

### Goals
- **Persistent Visibility:** When a radio station is active (playing, buffering, or paused), display a compact mini player bar docked directly above the bottom `NavigationBar`.
- **Immediate Controls:** Provide a single-tap Play/Pause toggle on the right side of the bar that operates without opening the full player screen.
- **Easy Expansion:** Tapping the station icon, title, or the bar body transitions directly to the full `Route.RadioPlayer` screen.
- **Station Branding:** Show the station's square artwork thumbnail on the left, with an app fallback icon if artwork is unavailable.
- **Architecture Integrity:** Maintain strict module decoupling: `:feature:home` must not depend on `:feature:radio-player`. Integration happens via Compose slot APIs wired in `:feature:root`.

### Non-Goals
- Next/Previous track skipping from the mini player (reserved for the full player).
- Sleep timer countdown or sleep timer controls inside the mini player bar.
- Volume slider or scrubbing inside the mini player.
- Swipe-to-dismiss or close button to discard the playback session (playback continues until stopped or replaced).

### Anti-Goals
- Never obscure the `NavigationBar` items or system navigation gestures.
- Never add a direct Gradle dependency between `:feature:home` and `:feature:radio-player`.
- Avoid layout jumps or jank when transitioning playback states.

---

## 3. Architecture & Module Boundaries

### 3.1 Module Decoupling (Slot Pattern)
Per Simple-Radio's architecture rules (`AGENTS.md`), feature modules do not depend on other feature modules.
- **`:feature:home`:**
  - `HomeScreen` accepts an optional `@Composable () -> Unit` parameter: `miniPlayerContent`.
  - In `HomeScreen`, `Scaffold(bottomBar = { Column { miniPlayerContent(); NavigationBar { ... } } })`.
  - When `miniPlayerContent` produces no layout (e.g., hidden state), no extra spacing is consumed.
- **`:feature:radio-player`:**
  - Exposes `RadioPlayerBarFeature(onOpenPlayer: () -> Unit)` as a public composable entry point (similar to `RadioPlayerFeature`).
  - Internally uses a lightweight ViewModel (or shares player state from `RadioPlayer`) observing `RadioPlayerSnapshot` to render station artwork, title, and playback status.
  - Handles `Play` and `Pause` events directly via `RadioPlayer`.
- **`:feature:root`:**
  - In `Navigation.kt`, injects `miniPlayerContent = { RadioPlayerBarFeature(onOpenPlayer = ::showRadioPlayer) }` into `HomeFeature`.

---

## 4. UI Specification & Behavior

### 4.1 Layout & Visual Hierarchy
- **Container:** Rounded surface or docked card directly above `NavigationBar` with subtle border/elevation dividing it from the tab bar and content.
- **Left Element:**
  - Small square station icon (40dp x 40dp) with rounded corners (8dp).
  - Uses Coil's `AsyncImage` to render `artworkUri`, with placeholder/fallback vector icon (e.g., radio / music icon).
- **Center Element:**
  - Station name in `MaterialTheme.typography.titleMedium` or `bodyMedium` with single-line truncation (`TextOverflow.Ellipsis`).
  - Secondary status text or subtitle (optional/small): e.g. "Playing" / "Paused" or left clean with just station title to preserve compact height.
- **Right Element:**
  - Play/Pause icon button (e.g., `IconButton` with `Icons.Filled.PlayArrow` / `Icons.Filled.Pause`).
  - While buffering/connecting (`RadioPlaybackStatus.Connecting`), displays a compact indeterminate `CircularProgressIndicator` (size ~24dp).
- **Interactions:**
  - Clicking the Play/Pause icon button triggers play/pause without opening the player.
  - Clicking anywhere else on the bar (icon, text, background) invokes `onOpenPlayer()`.

### 4.2 Visibility Lifecycle
- **Fresh Launch / Idle:** When no station has been loaded or played (`RadioPlayerSnapshot.stationId == null` or status is `Idle` with no station), the mini player bar is not rendered (or occupies zero height).
- **Active Playback:** When a station is loaded (status is `Playing`, `Paused`, `Connecting`, or `Error`), the bar is visible across all three home tabs (Favorites, Search, Settings).
- **Navigation:** When the user taps the mini player and navigates to `Route.RadioPlayer`, the full player screen is displayed (the home bottom bar and mini player are naturally not present on that screen).

---

## 5. Testing & Verification Plan

1. **Unit Tests:**
   - Test mini player ViewModel / UI state mapping for `RadioPlayerSnapshot` changes (Playing, Paused, Connecting, Idle).
   - Test event handling for Play/Pause and interaction callbacks.
2. **Screenshot / Roborazzi Tests:**
   - Visual verification of the mini player bar in Playing and Paused states (light & dark theme).
   - Screenshot test of `HomeScreen` with the mini player docked above `NavigationBar`.
3. **Lint & Formatting:**
   - `./gradlew ktlintCheck`
   - `./gradlew lintDebug`
   - `./gradlew :feature:home:testDebugUnitTest`
   - `./gradlew :feature:radio-player:testDebugUnitTest`
   - `./gradlew :feature:root:testDebugUnitTest`
