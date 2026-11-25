package com.alxnophis.jetpack.search.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.search.data.model.Country
import com.alxnophis.jetpack.search.domain.usecase.GetCountriesUseCase
import com.alxnophis.jetpack.search.domain.usecase.ObserveFavoriteIdsUseCase
import com.alxnophis.jetpack.search.domain.usecase.SearchStationsUseCase
import com.alxnophis.jetpack.search.domain.usecase.SetStationFavoriteUseCase
import com.alxnophis.jetpack.search.ui.contract.SearchEvent
import com.alxnophis.jetpack.search.ui.contract.SearchStatus
import com.alxnophis.jetpack.search.ui.contract.SearchUiState
import com.alxnophis.jetpack.search.ui.contract.countries
import com.alxnophis.jetpack.search.ui.contract.favoriteIds
import com.alxnophis.jetpack.search.ui.contract.query
import com.alxnophis.jetpack.search.ui.contract.selectedCountry
import com.alxnophis.jetpack.search.ui.contract.stations
import com.alxnophis.jetpack.search.ui.contract.status
import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal const val SEARCH_DEBOUNCE_MILLIS = 400L

internal class SearchViewModel(
    private val getCountries: GetCountriesUseCase,
    private val searchStations: SearchStationsUseCase,
    private val observeFavoriteIds: ObserveFavoriteIdsUseCase,
    private val setStationFavorite: SetStationFavoriteUseCase,
    initialUiState: SearchUiState = SearchUiState.initialState,
) : BaseViewModel<SearchEvent, SearchUiState>(initialUiState) {
    private var searchJob: Job? = null

    init {
        loadCountries()
        observeFavorites()
        search()
    }

    override fun handleEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                updateUiState { copy { SearchUiState.query set event.query } }
                search(debounceMillis = SEARCH_DEBOUNCE_MILLIS)
            }

            is SearchEvent.CountrySelected -> {
                updateUiState { copy { SearchUiState.selectedCountry set event.country } }
                search()
            }

            is SearchEvent.ToggleFavorite -> {
                val favorite = event.station.id !in currentUiState.favoriteIds
                viewModelScope.launch { setStationFavorite(event.station, favorite) }
            }

            SearchEvent.Retry -> {
                if (currentUiState.countries.isEmpty()) loadCountries()
                search()
            }
        }
    }

    private fun loadCountries() {
        viewModelScope.launch {
            getCountries().onRight { countries: List<Country> ->
                updateUiState { copy { SearchUiState.countries set countries.toImmutableList() } }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoriteIds().collect { ids ->
                updateUiState { copy { SearchUiState.favoriteIds set ids.toImmutableSet() } }
            }
        }
    }

    private fun search(debounceMillis: Long = 0L) {
        searchJob?.cancel()
        searchJob =
            viewModelScope.launch {
                if (debounceMillis > 0) delay(debounceMillis)
                updateUiState { copy { SearchUiState.status set SearchStatus.Loading } }
                val state = currentUiState
                searchStations(name = state.query, countryCode = state.selectedCountry?.code).fold(
                    ifLeft = { showError() },
                    ifRight = { stations -> showStations(stations) },
                )
            }
    }

    private fun showStations(stations: List<FavoriteStation>) {
        updateUiState {
            copy {
                SearchUiState.stations set stations.toImmutableList()
                SearchUiState.status set if (stations.isEmpty()) SearchStatus.Empty else SearchStatus.Content
            }
        }
    }

    private fun showError() {
        updateUiState { copy { SearchUiState.status set SearchStatus.Error } }
    }
}
