package com.alxnophis.simpleradio.radioplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlaybackStatus
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlayer
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlayerSnapshot
import com.alxnophis.simpleradio.radioplayer.ui.contract.RadioPlayerBarEvent
import com.alxnophis.simpleradio.radioplayer.ui.contract.RadioPlayerBarUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class RadioPlayerBarViewModel(
    private val player: RadioPlayer,
    initialUiState: RadioPlayerBarUiState = RadioPlayerBarUiState.initialState,
) : ViewModel() {
    init {
        player.connect()
    }

    val uiState: StateFlow<RadioPlayerBarUiState> =
        player.snapshot
            .map { snapshot -> snapshot.toUiState() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = STOP_TIMEOUT_MILLIS),
                initialValue = initialUiState,
            )

    fun handleEvent(event: RadioPlayerBarEvent) {
        when (event) {
            RadioPlayerBarEvent.TogglePlayPause -> {
                if (player.snapshot.value.status == RadioPlaybackStatus.Playing) {
                    player.pause()
                } else {
                    player.play()
                }
            }

            RadioPlayerBarEvent.Play -> {
                player.play()
            }

            RadioPlayerBarEvent.Pause -> {
                player.pause()
            }
        }
    }

    private fun RadioPlayerSnapshot.toUiState(): RadioPlayerBarUiState {
        val hasStation = !stationName.isNullOrBlank()
        val isVisible = hasStation && status != RadioPlaybackStatus.Idle
        return RadioPlayerBarUiState(
            isVisible = isVisible,
            stationName = stationName,
            artworkUri = artworkUri?.toString(),
            status = status,
        )
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
