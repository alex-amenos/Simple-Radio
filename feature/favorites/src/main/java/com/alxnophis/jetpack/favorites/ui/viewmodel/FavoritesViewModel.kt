package com.alxnophis.jetpack.favorites.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.favorites.domain.usecase.ObserveFavoriteStationsUseCase
import com.alxnophis.jetpack.favorites.domain.usecase.RemoveFavoriteStationUseCase
import com.alxnophis.jetpack.favorites.ui.contract.FavoritesEvent
import com.alxnophis.jetpack.favorites.ui.contract.FavoritesStatus
import com.alxnophis.jetpack.favorites.ui.contract.FavoritesUiState
import com.alxnophis.jetpack.favorites.ui.contract.stations
import com.alxnophis.jetpack.favorites.ui.contract.status
import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

internal class FavoritesViewModel(
    private val observeFavoriteStations: ObserveFavoriteStationsUseCase,
    private val removeFavoriteStation: RemoveFavoriteStationUseCase,
    initialUiState: FavoritesUiState = FavoritesUiState.initialState,
) : BaseViewModel<FavoritesEvent, FavoritesUiState>(initialUiState) {
    init {
        viewModelScope.launch {
            observeFavoriteStations().collect { stations -> showStations(stations) }
        }
    }

    override fun handleEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.Remove -> viewModelScope.launch { removeFavoriteStation(event.stationId) }
        }
    }

    private fun showStations(stations: List<FavoriteStation>) {
        updateUiState {
            copy {
                FavoritesUiState.stations set stations.toImmutableList()
                FavoritesUiState.status set if (stations.isEmpty()) FavoritesStatus.Empty else FavoritesStatus.Content
            }
        }
    }
}
