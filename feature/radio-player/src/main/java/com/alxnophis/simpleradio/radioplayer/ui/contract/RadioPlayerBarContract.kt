package com.alxnophis.simpleradio.radioplayer.ui.contract

import androidx.compose.runtime.Immutable
import com.alxnophis.simpleradio.core.ui.viewmodel.UiEvent
import com.alxnophis.simpleradio.core.ui.viewmodel.UiState
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlaybackStatus

@Immutable
internal data class RadioPlayerBarUiState(
    val isVisible: Boolean,
    val stationName: String?,
    val artworkUri: String?,
    val status: RadioPlaybackStatus,
) : UiState {
    internal companion object {
        val initialState =
            RadioPlayerBarUiState(
                isVisible = false,
                stationName = null,
                artworkUri = null,
                status = RadioPlaybackStatus.Idle,
            )
    }
}

internal sealed interface RadioPlayerBarEvent : UiEvent {
    data object TogglePlayPause : RadioPlayerBarEvent

    data object Play : RadioPlayerBarEvent

    data object Pause : RadioPlayerBarEvent
}
