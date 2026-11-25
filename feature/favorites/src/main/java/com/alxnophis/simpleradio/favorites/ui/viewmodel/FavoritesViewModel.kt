package com.alxnophis.simpleradio.favorites.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alxnophis.simpleradio.favorites.domain.usecase.ObserveFavoriteStationsUseCase
import com.alxnophis.simpleradio.favorites.domain.usecase.RemoveFavoriteStationUseCase
import com.alxnophis.simpleradio.favorites.ui.contract.FavoritesEvent
import com.alxnophis.simpleradio.favorites.ui.contract.FavoritesStatus
import com.alxnophis.simpleradio.favorites.ui.contract.FavoritesUiState
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class FavoritesViewModel(
    observeFavoriteStations: ObserveFavoriteStationsUseCase,
    private val removeFavoriteStation: RemoveFavoriteStationUseCase,
    initialUiState: FavoritesUiState = FavoritesUiState.initialState,
) : ViewModel() {
    val uiState: StateFlow<FavoritesUiState> =
        observeFavoriteStations()
            .map { stations -> stations.toUiState() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = STOP_TIMEOUT_MILLIS),
                initialValue = initialUiState,
            )

    fun handleEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.Remove -> viewModelScope.launch { removeFavoriteStation(event.stationId) }
        }
    }

    private fun List<FavoriteStation>.toUiState(): FavoritesUiState =
        FavoritesUiState(
            status = if (isEmpty()) FavoritesStatus.Empty else FavoritesStatus.Content,
            stations = toImmutableList(),
        )

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
