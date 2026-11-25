package com.alxnophis.jetpack.radioplayer.ui.composable.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.alxnophis.jetpack.radioplayer.data.model.RadioStation
import com.alxnophis.jetpack.radioplayer.ui.contract.RadioStationListStatus
import com.alxnophis.jetpack.radioplayer.ui.contract.RadioStationListUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

internal class RadioStationListPreviewProvider : PreviewParameterProvider<RadioStationListUiState> {
    override val values: Sequence<RadioStationListUiState>
        get() =
            sequenceOf(
                RadioStationListUiState(
                    status = RadioStationListStatus.Content,
                    stations = stations.toImmutableList(),
                    currentStationId = "2",
                ),
                RadioStationListUiState(
                    status = RadioStationListStatus.Content,
                    stations = stations.toImmutableList(),
                ),
                RadioStationListUiState(
                    status = RadioStationListStatus.Empty,
                    stations = persistentListOf(),
                ),
                RadioStationListUiState(
                    status = RadioStationListStatus.Error,
                    stations = persistentListOf(),
                ),
            )

    private companion object {
        val stations =
            listOf(
                RadioStation("1", "Cadena SER", "https://example.com/ser", null, "MP3", 128),
                RadioStation("2", "Catalunya Ràdio", "https://example.com/catalunya", null, "AAC", 96),
                RadioStation("3", "Radio Nacional", "https://example.com/rne", null, "MP3", 192),
            )
    }
}
