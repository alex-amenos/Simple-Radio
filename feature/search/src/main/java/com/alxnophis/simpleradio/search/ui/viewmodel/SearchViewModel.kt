package com.alxnophis.simpleradio.search.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alxnophis.simpleradio.search.data.model.Country
import com.alxnophis.simpleradio.search.domain.usecase.ObserveFavoriteIdsUseCase
import com.alxnophis.simpleradio.search.domain.usecase.SearchStationsUseCase
import com.alxnophis.simpleradio.search.domain.usecase.SetStationFavoriteUseCase
import com.alxnophis.simpleradio.search.ui.contract.SearchEvent
import com.alxnophis.simpleradio.search.ui.contract.SearchStatus
import com.alxnophis.simpleradio.search.ui.contract.SearchUiState
import com.alxnophis.simpleradio.search.ui.model.SearchStationUiModel
import com.alxnophis.simpleradio.settings.data.repository.SettingsRepository
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal const val SEARCH_DEBOUNCE_MILLIS = 400L
internal const val KEY_SEARCH_REQUEST = "search_request"

@OptIn(ExperimentalCoroutinesApi::class)
internal class SearchViewModel(
    private val searchStations: SearchStationsUseCase,
    observeFavoriteIds: ObserveFavoriteIdsUseCase,
    private val setStationFavorite: SetStationFavoriteUseCase,
    private val settingsRepository: SettingsRepository? = null,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val isInitialLaunch: Boolean = !savedStateHandle.contains(KEY_SEARCH_REQUEST)

    private val request: StateFlow<SearchRequest> = savedStateHandle.getStateFlow(KEY_SEARCH_REQUEST, SearchRequest())

    private val results: Flow<SearchResults> = request.flatMapLatest { current -> search(current) }

    init {
        if (isInitialLaunch) {
            viewModelScope.launch {
                val preferences =
                    settingsRepository
                        ?.getSettingsFlow()
                        ?.firstOrNull()
                val code = preferences?.defaultCountryCode
                if (code != null && request.value.country == null) {
                    val name = preferences.defaultCountryName ?: code
                    updateRequest { copy(country = Country(code = code, name = name)) }
                }
            }
        }
    }

    val uiState: StateFlow<SearchUiState> =
        combine(
            request,
            results.onStart { emit(SearchResults(SearchStatus.Loading)) },
            observeFavoriteIds().onStart { emit(emptySet()) },
        ) { current, found, favoriteIds ->
            val favoriteSet = favoriteIds.toImmutableSet()
            val stationUiModels =
                found.items
                    .map { item ->
                        SearchStationUiModel(
                            station = item.station,
                            details = item.details,
                            isFavorite = item.station.id in favoriteSet,
                        )
                    }.toImmutableList()
            SearchUiState(
                status = found.status,
                query = current.query,
                selectedCountry = current.country,
                stations = stationUiModels,
                favoriteIds = favoriteSet,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = SearchUiState.initialState.copy(query = request.value.query, selectedCountry = request.value.country),
        )

    fun handleEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                updateRequest { copy(query = event.query, debounce = true) }
            }

            is SearchEvent.CountrySelected -> {
                updateRequest { copy(country = event.country, debounce = false) }
            }

            SearchEvent.Retry -> {
                updateRequest { copy(attempt = attempt + 1, debounce = false) }
            }

            is SearchEvent.ToggleFavorite -> {
                val favorite = event.station.id !in uiState.value.favoriteIds
                viewModelScope.launch { setStationFavorite(event.station, favorite) }
            }
        }
    }

    private fun search(searchRequest: SearchRequest): Flow<SearchResults> =
        flow {
            if (searchRequest.debounce) delay(SEARCH_DEBOUNCE_MILLIS)
            emit(SearchResults(SearchStatus.Loading))
            emit(
                searchStations(name = searchRequest.query, countryCode = searchRequest.country?.code).fold(
                    ifLeft = { SearchResults(SearchStatus.Error) },
                    ifRight = { stations -> stations.toSearchResults() },
                ),
            )
        }

    private fun updateRequest(reduce: SearchRequest.() -> SearchRequest) {
        savedStateHandle[KEY_SEARCH_REQUEST] = request.value.reduce()
    }

    private fun List<FavoriteStation>.toSearchResults(): SearchResults =
        SearchResults(
            status = if (isEmpty()) SearchStatus.Empty else SearchStatus.Content,
            items =
                map { station ->
                    SearchResultItem(
                        station = station,
                        details = listOfNotNull(station.country, station.codec, station.bitrate?.let { "$it kbps" }).joinToString(" · "),
                    )
                }.toImmutableList(),
        )

    private data class SearchResultItem(
        val station: FavoriteStation,
        val details: String,
    )

    private data class SearchResults(
        val status: SearchStatus,
        val items: ImmutableList<SearchResultItem> = persistentListOf(),
    )
}
