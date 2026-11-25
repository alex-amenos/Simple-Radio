package com.alxnophis.jetpack.radioplayer.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.radioplayer.data.model.RadioError
import com.alxnophis.jetpack.radioplayer.data.model.RadioStation
import com.alxnophis.jetpack.radioplayer.data.repository.RadioStationsRepository
import com.alxnophis.jetpack.radioplayer.playback.RadioPlayer
import com.alxnophis.jetpack.radioplayer.ui.contract.RadioStationListStatus
import com.alxnophis.jetpack.radioplayer.ui.contract.RadioStationListEvent
import com.alxnophis.jetpack.radioplayer.ui.contract.RadioStationListUiState
import com.alxnophis.jetpack.radioplayer.ui.contract.currentStationId
import com.alxnophis.jetpack.radioplayer.ui.contract.stations
import com.alxnophis.jetpack.radioplayer.ui.contract.status
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class RadioStationListViewModel(
    private val repository: RadioStationsRepository,
    private val player: RadioPlayer,
    initialUiState: RadioStationListUiState = RadioStationListUiState.initialState,
) : BaseViewModel<RadioStationListEvent, RadioStationListUiState>(initialUiState) {
    init {
        player.connect()
        load()
        viewModelScope.launch {
            player.snapshot
                .map { it.stationId }
                .distinctUntilChanged()
                .collect { stationId ->
                    updateUiState {
                        copy {
                            RadioStationListUiState.currentStationId set stationId
                        }
                    }
                }
        }
    }

    override fun handleEvent(event: RadioStationListEvent) {
        when (event) {
            RadioStationListEvent.Retry -> load()
            is RadioStationListEvent.Select -> player.playQueue(currentUiState.stations, event.index)
        }
    }

    private fun load() {
        updateUiState {
            copy {
                RadioStationListUiState.status set RadioStationListStatus.Loading
            }
        }
        viewModelScope.launch {
            repository.getSpanishStations().fold(
                ifLeft = { showError() },
                ifRight = { stations -> showStations(stations) },
            )
        }
    }

    private fun showStations(stations: List<RadioStation>) {
        updateUiState {
            copy {
                RadioStationListUiState.stations set stations.toImmutableList()
                RadioStationListUiState.status set if (stations.isEmpty()) RadioStationListStatus.Empty else RadioStationListStatus.Content
            }
        }
    }

    private fun showError() {
        updateUiState {
            copy {
                RadioStationListUiState.status set RadioStationListStatus.Error
            }
        }
    }
}
