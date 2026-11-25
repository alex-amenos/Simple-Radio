package com.alxnophis.simpleradio.radioplayer.ui.composable

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.alxnophis.simpleradio.core.ui.theme.AppTheme
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlaybackStatus
import com.alxnophis.simpleradio.radioplayer.ui.contract.RadioPlayerBarUiState
import com.alxnophis.simpleradio.testing.screenshot.ScreenshotTestUtils.captureScreenshot
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
internal class RadioPlayerBarScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun radioPlayerBarPlaying() {
        val uiState =
            RadioPlayerBarUiState(
                isVisible = true,
                stationName = "BBC Radio 1",
                artworkUri = null,
                status = RadioPlaybackStatus.Playing,
            )
        composeRule.setContent {
            AppTheme {
                RadioPlayerBar(
                    uiState = uiState,
                    onOpenPlayer = {},
                    onPlayPauseClick = {},
                )
            }
        }

        composeRule
            .onRoot()
            .captureScreenshot(screenName = "RadioPlayerBar", stateIndex = 0)
    }

    @Test
    fun radioPlayerBarPlayingDark() {
        val uiState =
            RadioPlayerBarUiState(
                isVisible = true,
                stationName = "BBC Radio 1",
                artworkUri = null,
                status = RadioPlaybackStatus.Playing,
            )
        composeRule.setContent {
            AppTheme(darkTheme = true) {
                RadioPlayerBar(
                    uiState = uiState,
                    onOpenPlayer = {},
                    onPlayPauseClick = {},
                )
            }
        }

        composeRule
            .onRoot()
            .captureScreenshot(screenName = "RadioPlayerBarDark", stateIndex = 0)
    }

    @Test
    fun radioPlayerBarPaused() {
        val uiState =
            RadioPlayerBarUiState(
                isVisible = true,
                stationName = "Cadena SER",
                artworkUri = null,
                status = RadioPlaybackStatus.Paused,
            )
        composeRule.setContent {
            AppTheme {
                RadioPlayerBar(
                    uiState = uiState,
                    onOpenPlayer = {},
                    onPlayPauseClick = {},
                )
            }
        }

        composeRule
            .onRoot()
            .captureScreenshot(screenName = "RadioPlayerBar", stateIndex = 1)
    }

    @Test
    fun radioPlayerBarBuffering() {
        val uiState =
            RadioPlayerBarUiState(
                isVisible = true,
                stationName = "Catalunya Ràdio",
                artworkUri = null,
                status = RadioPlaybackStatus.Buffering,
            )
        composeRule.setContent {
            AppTheme {
                RadioPlayerBar(
                    uiState = uiState,
                    onOpenPlayer = {},
                    onPlayPauseClick = {},
                )
            }
        }

        composeRule
            .onRoot()
            .captureScreenshot(screenName = "RadioPlayerBar", stateIndex = 2)
    }
}
