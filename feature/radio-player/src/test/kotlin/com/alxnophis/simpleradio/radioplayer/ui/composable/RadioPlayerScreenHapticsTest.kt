package com.alxnophis.simpleradio.radioplayer.ui.composable

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.alxnophis.simpleradio.core.ui.theme.AppTheme
import com.alxnophis.simpleradio.radioplayer.R
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlaybackStatus
import com.alxnophis.simpleradio.radioplayer.ui.contract.RadioPlayerEvent
import com.alxnophis.simpleradio.radioplayer.ui.contract.RadioPlayerUiState
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
internal class RadioPlayerScreenHapticsTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val haptic = RecordingHapticFeedback()
    private val events = mutableListOf<RadioPlayerEvent>()

    @Test
    fun `GIVEN playing screen WHEN previous is clicked THEN a context click haptic is performed and Previous is dispatched`() {
        setScreen(RadioPlaybackStatus.Playing)

        clickButton(R.string.radioplayer_previous)

        haptic.performed shouldBeEqualTo listOf(HapticFeedbackType.ContextClick)
        events shouldBeEqualTo listOf(RadioPlayerEvent.Previous)
    }

    @Test
    fun `GIVEN previous is disabled WHEN previous is clicked THEN no haptic is performed and Previous is not dispatched`() {
        setScreen(RadioPlaybackStatus.Playing, canPrevious = false)

        clickButton(R.string.radioplayer_previous)

        haptic.performed shouldBeEqualTo emptyList()
        events shouldBeEqualTo emptyList()
    }

    @Test
    fun `GIVEN playing screen WHEN pause is clicked THEN a context click haptic is performed and Pause is dispatched`() {
        setScreen(RadioPlaybackStatus.Playing)

        clickButton(R.string.radioplayer_pause)

        haptic.performed shouldBeEqualTo listOf(HapticFeedbackType.ContextClick)
        events shouldBeEqualTo listOf(RadioPlayerEvent.Pause)
    }

    @Test
    fun `GIVEN playing screen WHEN next is clicked THEN a context click haptic is performed and Next is dispatched`() {
        setScreen(RadioPlaybackStatus.Playing)

        clickButton(R.string.radioplayer_next)

        haptic.performed shouldBeEqualTo listOf(HapticFeedbackType.ContextClick)
        events shouldBeEqualTo listOf(RadioPlayerEvent.Next)
    }

    @Test
    fun `GIVEN playing screen WHEN stop is clicked THEN a context click haptic is performed and Stop is dispatched`() {
        setScreen(RadioPlaybackStatus.Playing)

        clickButton(R.string.radioplayer_stop)

        haptic.performed shouldBeEqualTo listOf(HapticFeedbackType.ContextClick)
        events shouldBeEqualTo listOf(RadioPlayerEvent.Stop)
    }

    @Test
    fun `GIVEN playing screen WHEN sleep timer is clicked THEN no haptic is performed`() {
        setScreen(RadioPlaybackStatus.Playing)

        clickButton(R.string.radioplayer_sleep_timer)

        haptic.performed shouldBeEqualTo emptyList()
    }

    private fun setScreen(
        status: RadioPlaybackStatus,
        canPrevious: Boolean = true,
    ) {
        composeRule.setContent {
            CompositionLocalProvider(LocalHapticFeedback provides haptic) {
                AppTheme {
                    RadioPlayerScreen(
                        uiState =
                            RadioPlayerUiState(
                                stationName = "Catalunya Ràdio",
                                artworkUri = null,
                                status = status,
                                canPrevious = canPrevious,
                                canNext = true,
                                sleepTimerRemainingMillis = null,
                            ),
                        onBack = {},
                        handleEvent = { events += it },
                    )
                }
            }
        }
    }

    private fun clickButton(descriptionRes: Int) {
        composeRule
            .onNodeWithContentDescription(RuntimeEnvironment.getApplication().getString(descriptionRes))
            .assertIsDisplayed()
            .performClick()
    }
}
