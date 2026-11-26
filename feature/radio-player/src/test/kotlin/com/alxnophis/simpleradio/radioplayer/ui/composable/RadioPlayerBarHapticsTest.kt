package com.alxnophis.simpleradio.radioplayer.ui.composable

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.alxnophis.simpleradio.core.ui.theme.AppTheme
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlaybackStatus
import com.alxnophis.simpleradio.radioplayer.ui.contract.RadioPlayerBarUiState
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class RadioPlayerBarHapticsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `GIVEN playing bar WHEN play pause is clicked THEN a context click haptic is performed and the click is dispatched`() {
        val haptic = RecordingHapticFeedback()
        var toggles = 0
        composeRule.setContent {
            CompositionLocalProvider(LocalHapticFeedback provides haptic) {
                AppTheme {
                    RadioPlayerBar(
                        uiState =
                            RadioPlayerBarUiState(
                                isVisible = true,
                                stationName = "BBC Radio 1",
                                artworkUri = null,
                                status = RadioPlaybackStatus.Playing,
                            ),
                        onOpenPlayer = {},
                        onPlayPauseClick = { toggles++ },
                    )
                }
            }
        }

        composeRule.onNodeWithTag(RadioPlayerBarTags.TAG_RADIO_PLAYER_BAR_PLAY_PAUSE).performClick()

        haptic.performed shouldBeEqualTo listOf(HapticFeedbackType.ContextClick)
        toggles shouldBeEqualTo 1
    }
}
