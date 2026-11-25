# Search Screen Optimization Design Specification

- **Date:** 2026-11-25
- **Status:** Approved
- **Scope:** `:shared:api`, `:shared:core`, `:shared:storage`, `:feature:search`, `:feature:settings`, `:feature:root`

---

## 1. Intent & Context
The search screen is a core discovery entry point in Simple Radio. Currently:
1. Every time search attempts to load countries, it hits the remote API endpoint `/json/countries` over the network, even though country lists are virtually static.
2. The search results list can drop frames during fast scrolling due to subcomposition overhead in `CoreStationIcon` (`SubcomposeAsyncImage`) and unskippable items.
3. Users cannot configure a preferred default country for station searches; every app launch starts with no country filter selected.

---

## 2. Requirements & Goals

### 2.1 30-Day Country Caching & Splash Pre-Warming
- **30-Day Expiration Policy:** Cache the countries list response locally on disk with a minimum 30-day (2,592,000 seconds) freshness window. Subsequent calls within this window must be served from cache without remote network requests.
- **Splash Screen Pre-Warming:** During application launch at the splash screen (`RootViewModel`), trigger pre-warming of the countries cache if missing or expired. If the network request takes longer than a safe timeout threshold (1,500 ms) or fails, the splash screen must not be blocked indefinitely.
- **Offline Availability:** If countries were previously cached, they must remain available offline even if network connectivity is absent.

### 2.2 Station List Compose Fluency
- **Subcomposition Elimination:** Replace Coil's `SubcomposeAsyncImage` in `CoreStationIcon` with `AsyncImage` and lightweight placeholder/fallback painters.
- **Recomposition Skipping & Stability:** Wrap or map `FavoriteStation` to an `@Immutable` UI model (`SearchStationUiModel`) so `SearchStationItem` is skippable by the Compose compiler.
- **Precomputed Metadata:** Station subtitle metadata (`country · codec · bitrate`) must be pre-formatted or memoized, avoiding repeated allocations during scroll passes.
- **LazyColumn Optimizations:** Use `contentType` on lazy list items to enable Compose node recycling.

### 2.3 Default Country Setting & Persistence
- **Settings Preference:** Extend `SettingsPreferences` to persist `defaultCountryCode: String?` (null represents "All countries").
- **Settings UI:** Add a "Default search country" preference in `SettingsScreen` that displays the current selection and opens a selection dialog/menu populated with the cached country list.
- **Search Screen Integration:** On fresh app launch, `SearchScreen` initializes its selected country filter with the user's default country from Settings.
- **Independent In-Session Selection:** Modifying the country filter on the search screen updates the active search request for the current session only; it does NOT alter the user's persisted default country in Settings.

---

## 3. Architecture & Data Flow

### 3.1 Country Caching via OkHttp Disk Cache
- Configure OkHttp `Cache` in `RadioBrowserRetrofitFactory` pointing to `context.cacheDir / "radio_browser_http_cache"` (10 MB).
- Add an interceptor that intercepts `/json/countries` requests and forces `Cache-Control: public, max-age=2592000` (30 days) on successful responses.
- In `NetworkStatusInterceptor`, when offline, allow cached responses for `/json/countries` by applying `max-stale=2592000`.

### 3.2 Shared Country Model & Repository
- Expose `Country` model and `CountriesRepository` (or `GetCountriesUseCase`) from `:shared:api` so both `:feature:search` and `:feature:settings` (and `:feature:root`) can retrieve cached countries cleanly without cross-feature dependencies.

### 3.3 UI Performance in `:feature:search`
- Introduce `SearchStationUiModel` with `@Immutable` annotations containing `station`, `details: String`, and `isFavorite: Boolean`.
- Update `CoreStationIcon` to use `AsyncImage` with a vector/shape fallback painter.

---

## 4. Non-Goals & Anti-Goals
- **Anti-Goal:** Stalling splash screen indefinitely on bad/offline network.
- **Anti-Goal:** Automatically overwriting the user's Settings default country when they change the filter on the search screen.
- **Non-Goal:** Full-text fuzzy search for countries (standard alphabetical list with code/name is sufficient).
