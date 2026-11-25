package com.alxnophis.simpleradio.radioplayer.ui.composable

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.alxnophis.simpleradio.core.ui.theme.AppTheme
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlaybackStatus
import com.alxnophis.simpleradio.radioplayer.ui.contract.RadioPlayerUiState
import com.alxnophis.simpleradio.testing.screenshot.ScreenshotTestUtils.captureScreenshot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w891dp-h411dp-land")
internal class RadioPlayerLandscapeScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun radioPlayerLandscape() {
        val uiState =
            RadioPlayerUiState(
                stationName = "Catalunya Ràdio",
                artworkUri = null,
                status = RadioPlaybackStatus.Playing,
                canPrevious = true,
                canNext = true,
                sleepTimerRemainingMillis = null,
            )
        composeRule.setContent {
            AppTheme {
                RadioPlayerScreen(
                    uiState,
                    onBack = {},
                    handleEvent = {},
                )
            }
        }

        composeRule
            .onRoot()
            .captureScreenshot(
                screenName = "RadioPlayerScreenLandscape",
                stateIndex = 0,
            )
    }
}
