# Search Screen Optimization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Optimize the search screen by adding a 30-day disk cache for countries with splash pre-warming, improving station list Compose rendering fluency, and persisting a default search country preference in Settings.

**Architecture:** OkHttp disk cache with a 30-day Cache-Control interceptor for countries in `:shared:api`; shared `Country` model and `CountriesRepository`; `AsyncImage` with skippable `@Immutable` UI models in `:shared:core` and `:feature:search`; DataStore persistence of default country in `:feature:settings`; and splash pre-warming in `:feature:root`.

**Tech Stack:** Kotlin 2.4.20, Jetpack Compose (BOM 2026.05.01), Coil 2.7.0, OkHttp 4.x / Retrofit, Arrow-kt 2.2.3, Koin 4.2.2, AndroidX DataStore, JUnit 5, Turbine.

**Spec:** `docs/superpowers/specs/2026-11-25-search-optimization-design.md`

## Global Constraints

- Never commit automatically without user review (AGENTS.md).
- Follow Conventional Commits (`feat:`, `refactor:`, `test:`, etc.).
- Feature modules must not have direct circular dependencies.
- No wildcard imports, sort imports alphabetically (Android, Third-party, Project).
- Maintain 100% passing tests via `./gradlew testDebugUnitTest`.

## Review Focus

- Cache header manipulation does not inadvertently cache dynamic search station endpoints.
- Network offline state returns cached countries when within 30 days instead of failing with network error.
- Splash screen dismissal is never delayed indefinitely if country fetch times out or fails.
- Changing search filters inside `SearchScreen` never overwrites the persisted setting in DataStore.
- `CoreStationIcon` displays appropriate fallback icon when favicon URL is null, empty, or fails to load.

---

### Task 1: OkHttp 30-Day Response Cache for Countries in `:shared:api`

**Files:**
- Create: `shared/api/src/main/java/com/alxnophis/simpleradio/api/interceptor/CountryCacheInterceptor.kt`
- Modify: `shared/api/src/main/java/com/alxnophis/simpleradio/api/radiobrowser/RadioBrowserRetrofitFactory.kt`
- Test: `shared/api/src/test/java/com/alxnophis/simpleradio/api/interceptor/CountryCacheInterceptorTest.kt`

**Interfaces:**
- Produces: `CountryCacheInterceptor` (OkHttp interceptor adding 30-day cache control headers for `/json/countries`).

- [ ] **Step 1: Write the failing test for CountryCacheInterceptor**

Test that responses for `/json/countries` receive `Cache-Control: public, max-age=2592000`, whereas other endpoints (like `/json/stations/search`) leave response headers unmodified.

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :shared:api:testDebugUnitTest --tests "*.CountryCacheInterceptorTest"`
Expected: FAIL (class not found)

- [ ] **Step 3: Implement CountryCacheInterceptor and configure OkHttpClient cache in RadioBrowserRetrofitFactory**

Create `CountryCacheInterceptor` in `shared/api/src/main/java/com/alxnophis/simpleradio/api/interceptor/CountryCacheInterceptor.kt`.
In `RadioBrowserRetrofitFactory`, configure `okhttp3.Cache(File(context.cacheDir, "radio_browser_http_cache"), 10L * 1024 * 1024)` and add `CountryCacheInterceptor`.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :shared:api:testDebugUnitTest --tests "*.CountryCacheInterceptorTest"`
Expected: PASS

---

### Task 2: Shared Countries Model & Repository in `:shared:api`

**Files:**
- Create: `shared/api/src/main/java/com/alxnophis/simpleradio/api/model/Country.kt`
- Create: `shared/api/src/main/java/com/alxnophis/simpleradio/api/repository/CountriesRepository.kt`
- Create: `shared/api/src/main/java/com/alxnophis/simpleradio/api/repository/CountriesRepositoryImpl.kt`
- Modify: `shared/api/src/main/java/com/alxnophis/simpleradio/api/di/ApiModule.kt`
- Modify: `feature/search/src/main/java/com/alxnophis/simpleradio/search/data/repository/SearchStationsRepositoryImpl.kt`
- Test: `shared/api/src/test/java/com/alxnophis/simpleradio/api/repository/CountriesRepositoryImplTest.kt`

**Interfaces:**
- Produces: `Country(val name: String, val code: String)`, `CountriesRepository { suspend fun getCountries(): Either<CallError, List<Country>> }`
- Consumes: `RadioBrowserSearchService`

- [ ] **Step 1: Write the failing test for CountriesRepositoryImpl**

Test that `CountriesRepositoryImpl` maps `CountryApiModel` to `Country`, filters nulls/blank codes, removes duplicates by code, and sorts alphabetically by name.

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :shared:api:testDebugUnitTest --tests "*.CountriesRepositoryImplTest"`
Expected: FAIL

- [ ] **Step 3: Implement Country, CountriesRepository, and CountriesRepositoryImpl**

Implement model and repository in `:shared:api`. Register `CountriesRepository` in Koin `apiModule`. Update `SearchStationsRepositoryImpl` to delegate country fetching to `CountriesRepository`.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :shared:api:testDebugUnitTest --tests "*.CountriesRepositoryImplTest"`
Expected: PASS

---

### Task 3: Compose Fluency in CoreStationIcon and SearchScreen

**Files:**
- Modify: `shared/core/src/main/java/com/alxnophis/simpleradio/core/ui/composable/CoreStationIcon.kt`
- Create: `feature/search/src/main/java/com/alxnophis/simpleradio/search/ui/model/SearchStationUiModel.kt`
- Modify: `feature/search/src/main/java/com/alxnophis/simpleradio/search/ui/contract/SearchContract.kt`
- Modify: `feature/search/src/main/java/com/alxnophis/simpleradio/search/ui/composable/SearchScreen.kt`
- Modify: `feature/search/src/main/java/com/alxnophis/simpleradio/search/ui/viewmodel/SearchViewModel.kt`
- Test: `feature/search/src/test/java/com/alxnophis/simpleradio/search/ui/SearchViewModelUnitTests.kt`

**Interfaces:**
- Produces: `SearchStationUiModel(val station: FavoriteStation, val details: String, val isFavorite: Boolean)`
- Consumes: `CoreStationIcon`, `FavoriteStation`

- [ ] **Step 1: Write unit tests verifying SearchStationUiModel mapping**

Verify that search stations are mapped into immutable UI models with formatted details strings (`"country · codec · bitrate kbps"`).

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :feature:search:testDebugUnitTest --tests "*.SearchViewModelUnitTests"`
Expected: Compile failure / missing UI model

- [ ] **Step 3: Implement AsyncImage in CoreStationIcon and optimize SearchScreen**

In `CoreStationIcon`, replace `SubcomposeAsyncImage` with `AsyncImage` using vector painter fallback.
In `SearchScreen`, use `itemsIndexed` with `key`, `contentType = { _, _ -> "station" }`, and `SearchStationUiModel`.

- [ ] **Step 4: Run tests to verify all tests pass**

Run: `./gradlew :feature:search:testDebugUnitTest`
Expected: PASS

---

### Task 4: Default Country Preference in SettingsPreferences and SettingsRepository

**Files:**
- Modify: `feature/settings/src/main/java/com/alxnophis/simpleradio/settings/data/model/SettingsPreferences.kt`
- Modify: `feature/settings/src/main/java/com/alxnophis/simpleradio/settings/data/repository/SettingsRepository.kt`
- Modify: `feature/settings/src/main/java/com/alxnophis/simpleradio/settings/data/repository/SettingsRepositoryImpl.kt`
- Test: `feature/settings/src/test/java/com/alxnophis/simpleradio/settings/data/model/SettingsPreferencesSerializerTest.kt`
- Test: `feature/settings/src/test/java/com/alxnophis/simpleradio/settings/data/repository/SettingsRepositoryImplTest.kt`

**Interfaces:**
- Produces: `SettingsPreferences.defaultCountryCode: String?`, `SettingsRepository.updateDefaultCountryCode(countryCode: String?): Either<SettingsPreferencesError, Unit>`

- [ ] **Step 1: Write failing test for defaultCountryCode serialization**

Test reading and writing `SettingsPreferences` with `defaultCountryCode = "ES"` and `defaultCountryCode = null`.

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :feature:settings:testDebugUnitTest --tests "*.SettingsPreferencesSerializerTest"`
Expected: FAIL

- [ ] **Step 3: Implement defaultCountryCode in SettingsPreferences and SettingsRepository**

Update `SettingsPreferences` with `val defaultCountryCode: String? = null`. Add `updateDefaultCountryCode` to `SettingsRepository` and `SettingsRepositoryImpl`.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :feature:settings:testDebugUnitTest`
Expected: PASS

---

### Task 5: Default Country Preference UI in SettingsScreen

**Files:**
- Modify: `feature/settings/build.gradle`
- Create: `feature/settings/src/main/java/com/alxnophis/simpleradio/settings/ui/composable/SettingsCountryItem.kt`
- Modify: `feature/settings/src/main/java/com/alxnophis/simpleradio/settings/ui/composable/SettingsScreen.kt`
- Modify: `feature/settings/src/main/java/com/alxnophis/simpleradio/settings/ui/contract/SettingsContract.kt`
- Modify: `feature/settings/src/main/java/com/alxnophis/simpleradio/settings/ui/viewmodel/SettingsViewModel.kt`
- Test: `feature/settings/src/test/java/com/alxnophis/simpleradio/settings/viewmodel/SettingsViewModelUnitTests.kt`

**Interfaces:**
- Produces: `SettingsUiEvent.SetDefaultCountry(countryCode: String?)`, `SettingsUiState.defaultCountryCode: String?`, `SettingsUiState.countries: ImmutableList<Country>`
- Consumes: `CountriesRepository` (from `:shared:api`)

- [ ] **Step 1: Write failing tests in SettingsViewModelUnitTests**

Test that `SettingsViewModel` exposes the current default country code and handles `SetDefaultCountry` events by invoking `settingsRepository.updateDefaultCountryCode`.

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :feature:settings:testDebugUnitTest --tests "*.SettingsViewModelUnitTests"`
Expected: FAIL

- [ ] **Step 3: Implement SettingsCountryItem, wire CountriesRepository into SettingsViewModel and update SettingsScreen**

Add `SHARED_API` dependency to `feature/settings/build.gradle`. In `SettingsScreen`, render `SettingsCountryItem` under a search preferences section, showing the country picker with "All countries" and available countries.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :feature:settings:testDebugUnitTest --tests "*.SettingsViewModelUnitTests"`
Expected: PASS

---

### Task 6: Splash Pre-Warming & Search Country Initialization on Fresh Launch

**Files:**
- Modify: `feature/root/src/main/java/com/alxnophis/simpleradio/root/ui/viewmodel/RootViewModel.kt`
- Modify: `feature/search/src/main/java/com/alxnophis/simpleradio/search/ui/viewmodel/SearchViewModel.kt`
- Modify: `feature/search/src/main/java/com/alxnophis/simpleradio/search/di/SearchModule.kt`
- Modify: `feature/root/src/main/java/com/alxnophis/simpleradio/root/di/RootModule.kt`
- Test: `feature/root/src/test/java/com/alxnophis/simpleradio/root/ui/viewmodel/RootViewModelUnitTests.kt`
- Test: `feature/search/src/test/java/com/alxnophis/simpleradio/search/ui/SearchViewModelUnitTests.kt`

**Interfaces:**
- Consumes: `CountriesRepository.getCountries()`, `SettingsRepository.getSettingsFlow()`

- [ ] **Step 1: Write failing tests in RootViewModelUnitTests and SearchViewModelUnitTests**

In `RootViewModelUnitTests`, verify that countries pre-warming is initiated on start without blocking `isReady`.
In `SearchViewModelUnitTests`, verify that on fresh launch without a previous `SavedStateHandle` value, `selectedCountry` is initialized from the default country setting, and subsequent search country selection does not mutate settings.

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :feature:root:testDebugUnitTest :feature:search:testDebugUnitTest`
Expected: FAIL

- [ ] **Step 3: Implement splash pre-warming in RootViewModel and default country initialization in SearchViewModel**

In `RootViewModel`, launch a non-blocking coroutine with `withTimeoutOrNull(1500L)` calling `countriesRepository.getCountries()`.
In `SearchViewModel`, read the initial default country code on first launch and match it to the country list.

- [ ] **Step 4: Run all unit tests to verify they pass**

Run: `./gradlew testDebugUnitTest`
Expected: PASS
