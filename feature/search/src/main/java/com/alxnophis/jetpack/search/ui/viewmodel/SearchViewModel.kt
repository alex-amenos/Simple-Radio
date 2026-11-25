package com.alxnophis.jetpack.search.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.search.data.model.Country
import com.alxnophis.jetpack.search.domain.usecase.GetCountriesUseCase
import com.alxnophis.jetpack.search.domain.usecase.ObserveFavoriteIdsUseCase
import com.alxnophis.jetpack.search.domain.usecase.SearchStationsUseCase
import com.alxnophis.jetpack.search.domain.usecase.SetStationFavoriteUseCase
import com.alxnophis.jetpack.search.ui.contract.SearchEvent
import com.alxnophis.jetpack.search.ui.contract.SearchStatus
import com.alxnophis.jetpack.search.ui.contract.SearchUiState
import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

internal const val SEARCH_DEBOUNCE_MILLIS = 400L
internal const val KEY_SEARCH_REQUEST = "search_request"

@OptIn(ExperimentalCoroutinesApi::class)
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
        request.flatMapLatest { current -> search(current) }

    // Retries load countries again only until the first success.
    private val countries: Flow<ImmutableList<Country>> =
        request
            .map { it.attempt }
            .distinctUntilChanged()
            .transformLatest { getCountries().onRight { countries -> emit(countries.toImmutableList()) } }
            .take(1)
            .onStart { emit(persistentListOf()) }

    // Lazily: tab switches remove the screen but keep this view model, and results must not be fetched again.
    val uiState: StateFlow<SearchUiState> =
        combine(request, results, countries, observeFavoriteIds()) { current, found, countryList, favoriteIds ->
            SearchUiState(
                status = found.status,
                query = current.query,
                selectedCountry = current.country,
                countries = countryList,
                stations = found.stations,
                favoriteIds = favoriteIds.toImmutableSet(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = SearchUiState.initialState.copy(query = request.value.query, selectedCountry = request.value.country),
        )

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

    // Events arrive on the main thread, so this read-modify-write cannot race.
    private fun updateRequest(reduce: SearchRequest.() -> SearchRequest) {
        savedStateHandle[KEY_SEARCH_REQUEST] = request.value.reduce()
    }

    private fun List<FavoriteStation>.toSearchResults(): SearchResults =
        SearchResults(
            status = if (isEmpty()) SearchStatus.Empty else SearchStatus.Content,
            stations = toImmutableList(),
        )

    private data class SearchResults(
        val status: SearchStatus,
        val stations: ImmutableList<FavoriteStation> = persistentListOf(),
    )
}
