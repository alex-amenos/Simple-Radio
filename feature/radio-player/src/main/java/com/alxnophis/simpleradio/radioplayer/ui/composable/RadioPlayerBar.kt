package com.alxnophis.simpleradio.radioplayer.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.alxnophis.simpleradio.core.ui.composable.CoreStationIcon
import com.alxnophis.simpleradio.core.ui.theme.AppTheme
import com.alxnophis.simpleradio.radioplayer.R
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlaybackStatus
import com.alxnophis.simpleradio.radioplayer.ui.contract.RadioPlayerBarEvent
import com.alxnophis.simpleradio.radioplayer.ui.contract.RadioPlayerBarUiState
import com.alxnophis.simpleradio.radioplayer.ui.viewmodel.RadioPlayerBarViewModel

private val MINI_PLAYER_HEIGHT = 64.dp
private val ICON_SIZE = 40.dp
private val ACTION_BUTTON_SIZE = 48.dp
private val PROGRESS_INDICATOR_SIZE = 24.dp

@Composable
fun RadioPlayerBarFeature(
    onOpenPlayer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<RadioPlayerBarViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isVisible) {
        RadioPlayerBar(
            uiState = uiState,
            onOpenPlayer = onOpenPlayer,
            onPlayPauseClick = { viewModel.handleEvent(RadioPlayerBarEvent.TogglePlayPause) },
            modifier = modifier,
        )
    }
}

@Composable
internal fun RadioPlayerBar(
    uiState: RadioPlayerBarUiState,
    onOpenPlayer: () -> Unit,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .height(MINI_PLAYER_HEIGHT)
                .testTag(RadioPlayerBarTags.TAG_RADIO_PLAYER_BAR)
                .clickable(
                    onClickLabel = stringResource(R.string.radioplayer_now_playing),
                    onClick = onOpenPlayer,
                ),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 3.dp,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CoreStationIcon(
                faviconUrl = uiState.artworkUri,
                size = ICON_SIZE,
            )
            Text(
                text = uiState.stationName.orEmpty(),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier.size(ACTION_BUTTON_SIZE),
                contentAlignment = Alignment.Center,
            ) {
                if (uiState.status == RadioPlaybackStatus.Buffering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(PROGRESS_INDICATOR_SIZE),
                        strokeWidth = 2.5.dp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    val isPlaying = uiState.status == RadioPlaybackStatus.Playing
                    val icon = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow
                    val contentDesc =
                        stringResource(
                            if (isPlaying) R.string.radioplayer_pause else R.string.radioplayer_play,
                        )
                    val haptic = LocalHapticFeedback.current
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            onPlayPauseClick()
                        },
                        modifier =
                            Modifier
                                .size(ACTION_BUTTON_SIZE)
                                .testTag(RadioPlayerBarTags.TAG_RADIO_PLAYER_BAR_PLAY_PAUSE),
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = contentDesc,
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun RadioPlayerBarPreview() {
    AppTheme {
        RadioPlayerBar(
            uiState =
                RadioPlayerBarUiState(
                    isVisible = true,
                    stationName = "Catalunya Ràdio",
                    artworkUri = null,
                    status = RadioPlaybackStatus.Playing,
                ),
            onOpenPlayer = {},
            onPlayPauseClick = {},
        )
    }
}
