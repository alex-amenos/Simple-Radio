# ADR 0004: Deriving UiState with `stateIn` in ViewModels

- **Status:** Proposed
- **Date:** 2026-11-25
- **Deciders:** Architecture Team

---

## Context

`AGENTS.md` already allows two ViewModel styles:

- `BaseViewModel<Event, State>`: a `MutableStateFlow` mutated imperatively with `updateUiState { copy { ... } }`.
- A plain `androidx.lifecycle.ViewModel` that **derives** its state with `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialValue)`. `SettingsViewModel` and `RootViewModel` are the references.

The radio ViewModels (`FavoritesViewModel`, `SearchViewModel`, `RadioPlayerViewModel`, `HomeViewModel`) all extend `BaseViewModel`. Most of them only do this:

```kotlin
init { viewModelScope.launch { source.collect { updateUiState { copy { ... } } } } }
```

That has three costs:

- Upstream flows (Room, the Media3 controller) are collected eagerly for the whole ViewModel lifetime, even when no UI is subscribed.
- State that is a pure function of flows is written imperatively, which is boilerplate.
- Several coroutines write to one state object, so the final state depends on emission order.

None of these ViewModels pass a `SavedStateHandle` to `BaseViewModel` today. Moving away from it does not lose any persisted state, and `SearchViewModel` gains real process-death restoration (see below).

## Review of current ViewModels

| ViewModel | Recommendation | Sharing strategy |
|---|---|---|
| `FavoritesViewModel` | Plain `ViewModel` + `stateIn`. Best fit. | `WhileSubscribed(5_000)` |
| `RadioPlayerViewModel` | Plain `ViewModel` + `combine` + `stateIn`. | `WhileSubscribed(5_000)` |
| `SearchViewModel` | Plain `ViewModel` + declarative pipeline + `stateIn`, with the request in `SavedStateHandle`. | `Lazily` (see below) |
| `HomeViewModel` | Keep it. Optionally use `SavedStateHandle.getStateFlow`. | n/a |

### `FavoritesViewModel`

The state is a pure mapping of one Room flow. Remove is a write, and the flow re-emits on its own.

```kotlin
internal class FavoritesViewModel(
    observeFavoriteStations: ObserveFavoriteStationsUseCase,
    private val removeFavoriteStation: RemoveFavoriteStationUseCase,
    initialUiState: FavoritesUiState = FavoritesUiState.initialState,
) : ViewModel() {
    val uiState: StateFlow<FavoritesUiState> =
        observeFavoriteStations()
            .map { stations ->
                FavoritesUiState(
                    status = if (stations.isEmpty()) FavoritesStatus.Empty else FavoritesStatus.Content,
                    stations = stations.toImmutableList(),
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = initialUiState,
            )

    fun handleEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.Remove -> viewModelScope.launch { removeFavoriteStation(event.stationId) }
        }
    }
}
```

The Room query stops 5 s after the tab leaves composition. On return, the last value is replayed at once, so `Loading` does not flicker, and Room re-queries locally.

### `RadioPlayerViewModel`

Two collectors write today: the snapshot mapping and the sleep-timer ticker. Both become flows that are combined into one state:

```kotlin
private val sleepTimerRemaining: Flow<Long?> =
    player.snapshot
        .map { it.sleepTimerDeadlineMillis }
        .distinctUntilChanged()
        .flatMapLatest { deadline -> sleepTimerCountdown(deadline) }
        .catch { Timber.e(it, "Error in sleep timer countdown") }

private fun sleepTimerCountdown(deadline: Long?): Flow<Long?> =
    flow {
        if (deadline != null) {
            var remaining = deadline - clock()
            while (remaining > ZERO_MILLIS) {
                emit(remaining)
                delay(millisUntilDisplayChange(remaining))
                remaining = deadline - clock()
            }
        }
        emit(null)
    }

val uiState: StateFlow<RadioPlayerUiState> =
    combine(player.snapshot, sleepTimerRemaining) { snapshot, remaining ->
        RadioPlayerUiState(
            stationName = snapshot.stationName,
            artworkUri = snapshot.artworkUri?.toString(),
            status = snapshot.status,
            canPrevious = snapshot.hasPrevious,
            canNext = snapshot.hasNext,
            sleepTimerRemainingMillis = remaining,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialUiState)
```

- The countdown only ticks while the player screen is visible. It is computed from the deadline and the injected `clock`, so it stays correct after resubscribing.
- `player.connect()` stays in `init`. It is a side effect, not state.

### `SearchViewModel`

`query`, `selectedCountry`, retry and debounce are user inputs, so they live in one `SearchRequest`. Everything else is derived from it. The request is kept in `SavedStateHandle` and read with `getStateFlow`. The typed query and the chosen country then survive process death, and the search runs again on restore.

```kotlin
@Parcelize
internal data class SearchRequest(
    val query: String = "",
    val country: Country? = null,
    val attempt: Int = 0,
    val debounce: Boolean = false,
) : Parcelable

private data class SearchResults(
    val status: SearchStatus,
    val stations: ImmutableList<FavoriteStation> = persistentListOf(),
)

internal class SearchViewModel(
    private val getCountries: GetCountriesUseCase,
    private val searchStations: SearchStationsUseCase,
    observeFavoriteIds: ObserveFavoriteIdsUseCase,
    private val setStationFavorite: SetStationFavoriteUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val request: StateFlow<SearchRequest> =
        savedStateHandle.getStateFlow(KEY_SEARCH_REQUEST, SearchRequest())

    private val results: Flow<SearchResults> =
        request.flatMapLatest { req ->
            flow {
                if (req.debounce) delay(SEARCH_DEBOUNCE_MILLIS)
                emit(SearchResults(SearchStatus.Loading))
                emit(
                    searchStations(req.query, req.country?.code).fold(
                        ifLeft = { SearchResults(SearchStatus.Error) },
                        ifRight = { stations ->
                            SearchResults(
                                status = if (stations.isEmpty()) SearchStatus.Empty else SearchStatus.Content,
                                stations = stations.toImmutableList(),
                            )
                        },
                    ),
                )
            }
        }

    // Retries load countries again only until the first success.
    private val countries: Flow<ImmutableList<Country>> =
        request
            .map { it.attempt }
            .distinctUntilChanged()
            .transformLatest { getCountries().onRight { emit(it.toImmutableList()) } }
            .take(1)
            .onStart { emit(persistentListOf()) }

    val uiState: StateFlow<SearchUiState> =
        combine(request, results, countries, observeFavoriteIds()) { req, res, countries, favoriteIds ->
            SearchUiState(
                status = res.status,
                query = req.query,
                selectedCountry = req.country,
                countries = countries,
                stations = res.stations,
                favoriteIds = favoriteIds.toImmutableSet(),
            )
        }.stateIn(viewModelScope, SharingStarted.Lazily, SearchUiState.initialState)

    fun handleEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> updateRequest { copy(query = event.query, debounce = true) }
            is SearchEvent.CountrySelected -> updateRequest { copy(country = event.country, debounce = false) }
            SearchEvent.Retry -> updateRequest { copy(attempt = attempt + 1, debounce = false) }
            is SearchEvent.ToggleFavorite -> {
                val favorite = event.station.id !in uiState.value.favoriteIds
                viewModelScope.launch { setStationFavorite(event.station, favorite) }
            }
        }
    }

    // Events arrive on the main thread, so this read-modify-write cannot race.
    private fun updateRequest(reduce: SearchRequest.() -> SearchRequest) {
        savedStateHandle[KEY_SEARCH_REQUEST] = request.value.reduce()
    }

    private companion object {
        const val KEY_SEARCH_REQUEST = "search_request"
    }
}
```

This keeps today's behavior:

- The text field updates at once, because `query` comes from `request` and not from the debounced results.
- Typing waits 400 ms. Picking a country or tapping Retry searches immediately.
- A new request cancels the pending one through `flatMapLatest`. Picking a country during a pending debounce uses the latest query.

It also removes `searchJob`, the manual `cancel()`, and the reads of `currentUiState` used to build the request.

#### What is saved, and why

| Field | Saved? | Reason |
|---|---|---|
| `query`, `country` | Yes | User input. Losing it after process death is a visible bug. |
| `attempt`, `debounce` | Yes, as a side effect | They sit in the same `Parcelable`, so each event is one write and one emission. Splitting them into separate keys would emit twice per event, and the first emission would carry a stale `debounce` flag. That would show `Loading` and start a search that is cancelled at once. On restore they do no harm: at worst one 400 ms delay before the search. |
| `stations`, `status` | No | Large lists risk `TransactionTooLargeException`. They come back from the API by searching again. |
| `countries` | No | About 200 items. Fetched again on restore. |
| `favoriteIds` | No | Room is the source of truth. |

Required changes:

- `Country` gets `@Parcelize` and `: Parcelable`. The parcelize plugin is already applied by `common-android-base.gradle`.
- Koin: `viewModel { SearchViewModel(get(), get(), get(), get(), get()) }`. Koin 4.x resolves `SavedStateHandle` in `viewModel {}` definitions.
- Tests build the ViewModel with `SavedStateHandle()`. Restoring can be tested with `SavedStateHandle(mapOf("search_request" to SearchRequest(query = "jazz")))`, asserting that the search is called with `"jazz"`.

**Why `Lazily` and not `WhileSubscribed`:** tab content leaves composition on tab switch (`when (uiState.selectedTab)` in `HomeScreen`). The `SearchViewModel` survives because it is scoped to the Home nav entry. With `WhileSubscribed(5_000)`, returning to Search after 5 s would restart upstream. That would call the countries API again and re-run the search, briefly showing `Loading`. Search results come from request/response calls, not from an observable source, so `Lazily` keeps them. The only hot upstream left is the Room `observeFavoriteIds()` flow, which is cheap. `SavedStateHandle` covers process death, and `Lazily` covers tab switches; the two are complementary.

If `WhileSubscribed` is preferred later, the countries and the last search need caching in the repository first.

### `HomeViewModel`

Its state is purely event-driven (the selected tab), and no flow can be derived from it. `BaseViewModel` is fine here. An optional improvement is to keep the tab across process death:

```kotlin
internal class HomeViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    val uiState: StateFlow<HomeUiState> =
        savedStateHandle
            .getStateFlow(KEY_SELECTED_TAB, HomeTab.Favorites)
            .map { HomeUiState(selectedTab = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState.initialState)

    fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.SelectTab -> savedStateHandle[KEY_SELECTED_TAB] = event.tab
        }
    }
}
```

## Decision (proposal)

1. Migrate `FavoritesViewModel`, `RadioPlayerViewModel` and `SearchViewModel` from `BaseViewModel` to a plain `ViewModel`, deriving `uiState` with `stateIn` as above. Keep `handleEvent(event)` as a regular public function, like `SettingsViewModel`.
2. Use `SharingStarted.WhileSubscribed(5_000)` for observable sources (Room, the player). Use `SharingStarted.Lazily` when upstream is request-driven and lives under a tab (Search).
3. In `SearchViewModel`, keep user input (`SearchRequest`) in `SavedStateHandle` and read it with `getStateFlow`. Derived data (results, countries, favorites) is never saved; it is rebuilt from the request.
4. Keep the contracts (`UiState`, `UiEvent`, `@optics` data classes) unchanged. The composables keep calling `viewModel.uiState.collectAsStateWithLifecycle()`, so the UI does not change.
5. Keep `BaseViewModel` for purely event-driven screens (`HomeViewModel`) unless the `SavedStateHandle` option is chosen.
6. Ship one PR per ViewModel, smallest first: Favorites, then Radio player, then Search.

## Consequences and risks

- **Tests.** The tests read `currentUiState`, which comes from `BaseViewModel`. With `stateIn`, the value only updates while something collects, so tests must use Turbine (`viewModel.uiState.test { ... }`) as `AGENTS.md` recommends, or `backgroundScope.launch { viewModel.uiState.collect() }`. `BaseViewModelUnitTest` (main-dispatcher setup) can still be used.
- **Experimental API.** `flatMapLatest` and `transformLatest` need `@OptIn(ExperimentalCoroutinesApi::class)`.
- **Reading state in events.** `uiState.value` in `handleEvent` (Search `ToggleFavorite`) is safe because events come from a subscribed UI. This is the same as `SettingsViewModel.currentState`.
- **Optics.** `@optics` stays on the state classes, but these ViewModels build state with constructors, so `copy { }` is no longer needed in them.
- **Saved state size.** Only small user input goes into `SavedStateHandle`. Results must never be added there, to stay well under the Bundle limit.
- **Behavior.** No visible change is expected, apart from two things. Upstream collection stops 5 s after Favorites or the player leave the screen. Search now restores the query and country after process death.

## Alternatives considered

- **Keep `BaseViewModel` and add a `stateIn` helper to it.** This mixes two state owners (`_uiState` and the derived flow) in one class. Rejected now that dropping `BaseViewModel` is acceptable.
- **`SharingStarted.Eagerly` everywhere.** Less code, but upstream collection never stops.
- **Saving the whole `SearchUiState` (the `BaseViewModel` approach).** It would include the station and country lists, risking `TransactionTooLargeException`, and stale results would be shown after restore. Rejected in favor of saving only the request.
- **Separate `SavedStateHandle` keys for query and country.** Each event would need two writes and cause two emissions, which breaks the debounce (see the table above). Rejected.
- **`WhileSubscribed` for Search.** Causes refetches on tab switches unless repository caching is added first (see above).
