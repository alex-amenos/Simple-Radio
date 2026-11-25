# ADR 0004: Deriving UiState with `stateIn` in ViewModels

- **Status:** Proposed
- **Date:** 2026-11-25
- **Deciders:** Architecture Team

---

## Context

`BaseViewModel` owns a `MutableStateFlow<State>` (`_uiState`) that subclasses mutate through `updateUiState { copy { ... } }`. Every data source is collected inside `init { viewModelScope.launch { flow.collect { updateUiState { ... } } } }`.

Consequences of that pattern:

- Collection starts eagerly and never stops, even when no UI is subscribed (for example, the app is in the background). Room and player flows stay hot for the whole ViewModel lifetime.
- Imperative `collect` + `updateUiState` is boilerplate for state that is a pure function of upstream flows.
- Several sources writing to one state object makes the final state depend on emission order.

`stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial)` fixes the first two points: upstream is only collected while the UI is subscribed (5 s keeps it alive across configuration changes), and state is declared rather than assembled.

## Review of current ViewModels

| ViewModel | Current pattern | Fits `stateIn`? | Notes |
|---|---|---|---|
| `FavoritesViewModel` | `observeFavoriteStations().collect { showStations(it) }` | **Yes, best candidate** | State is a pure mapping of one flow: `stations` + `status` (Empty/Content). Events only trigger a DB write; the flow re-emits. |
| `SearchViewModel` (favorite ids) | `observeFavoriteIds().collect { ... favoriteIds }` | **Yes, partially** | Can be `combine`d into the state as a derived slice. |
| `SearchViewModel` (query/country/results) | `QueryChanged` → update state → `search()` with `searchJob`, manual debounce | **Yes, with a redesign** | Model `query` and `selectedCountry` as `MutableStateFlow`s; derive results with `combine(...).debounce().flatMapLatest { searchStations(...) }`. This removes `searchJob` and the manual `delay`. `flatMapLatest` gives the cancellation `searchJob?.cancel()` does today. |
| `SearchViewModel` (countries) | one-shot `loadCountries()` | Optional | One-shot call; could be `flow { emit(getCountries()) }` inside the same `combine`. Retry needs a trigger flow. |
| `RadioPlayerViewModel` (snapshot) | `player.snapshot.collect { updateUiState { ... } }` | **Yes** | Direct `map` of `snapshot` to `RadioPlayerUiState`. |
| `RadioPlayerViewModel` (sleep timer) | `collectLatest` + `tickSleepTimer` loop | **Yes, combined** | The ticker can be a `flatMapLatest` flow emitting remaining millis, `combine`d with the snapshot mapping. Keeps the existing `clock` injection for tests. |
| `HomeViewModel` | selected tab only | No | Pure event-driven local state; `MutableStateFlow` is the right tool. |

## Decision (proposal)

1. Add to `BaseViewModel` an opt-in way to expose a derived state while keeping the current API, e.g. a protected helper:

   ```kotlin
   protected fun Flow<State>.stateInViewModel(initial: State = _uiState.value): StateFlow<State> =
       stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), initial)
   ```

   with `STOP_TIMEOUT_MILLIS = 5_000`. Subclasses that need it override `uiState`; others are unaffected.

2. Migrate in this order, one PR each, smallest first:
   1. `FavoritesViewModel`: `uiState = observeFavoriteStations().map { it.toUiState() }.stateInViewModel()`.
   2. `RadioPlayerViewModel`: snapshot + sleep timer via `combine`.
   3. `SearchViewModel`: query/country flows + `flatMapLatest` search + favorite ids.

3. Keep `updateUiState` for purely event-driven state (`HomeViewModel`, transient flags).

## Trade-offs and risks

- **`SavedStateHandle` persistence.** `BaseViewModel` persists state inside `updateAndPersistState`. A derived `stateIn` bypasses that. Only `SearchViewModel` (query, country) has user input worth restoring; keep those two in `SavedStateHandle`-backed flows and derive the rest.
- **Tests.** `currentUiState` reads `_uiState.value`. With `WhileSubscribed`, a derived state has no value updates without a collector, so tests must collect (Turbine `test {}` or `backgroundScope.launch { uiState.collect() }`). Existing tests that assert `currentUiState` right after `init` need that change.
- **Mixed models.** If a ViewModel mixes derived and event-driven state, prefer a single `combine` over two writers, to avoid ordering bugs.
- **Mid-stream events.** One-time effects (navigation, snackbars) must not be modelled as state; they are out of scope here.

## Alternatives considered

- **Keep the current pattern.** Simplest, no test churn, but keeps eager collection and boilerplate.
- **`SharingStarted.Eagerly`.** Same lifetime as today, only less code. Rejected as it does not fix upstream lifetime.
- **`SharingStarted.Lazily`.** Never stops after first subscriber; marginal gain.

## Consequences

- No code changes in this PR. It only records the review and the proposal for discussion.
- Follow-up PRs implement the migration steps above after the decision is accepted.
