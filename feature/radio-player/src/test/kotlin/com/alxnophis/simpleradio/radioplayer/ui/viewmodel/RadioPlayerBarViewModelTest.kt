package com.alxnophis.simpleradio.radioplayer.ui.viewmodel

import android.net.Uri
import com.alxnophis.simpleradio.radioplayer.data.model.RadioStation
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlaybackStatus
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlayer
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlayerSnapshot
import com.alxnophis.simpleradio.radioplayer.ui.contract.RadioPlayerBarEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class RadioPlayerBarViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val uiCollector = CoroutineScope(dispatcher)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        uiCollector.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN idle player without station WHEN collected THEN isVisible is false`() {
        val player = FakeRadioPlayer(RadioPlayerSnapshot(stationName = null, status = RadioPlaybackStatus.Idle))
        val viewModel = RadioPlayerBarViewModel(player).subscribed()

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.value.isVisible shouldBeEqualTo false
    }

    @Test
    fun `GIVEN playing station WHEN collected THEN isVisible is true and properties match`() {
        val artworkUri =
            org.mockito.kotlin.mock<Uri> {
                org.mockito.kotlin
                    .whenever(it.toString())
                    .thenReturn("https://example.com/logo.png")
            }
        val player =
            FakeRadioPlayer(
                RadioPlayerSnapshot(
                    stationName = "BBC Radio 1",
                    artworkUri = artworkUri,
                    status = RadioPlaybackStatus.Playing,
                ),
            )
        val viewModel = RadioPlayerBarViewModel(player).subscribed()

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.value.isVisible shouldBeEqualTo true
        viewModel.uiState.value.stationName shouldBeEqualTo "BBC Radio 1"
        viewModel.uiState.value.artworkUri shouldBeEqualTo "https://example.com/logo.png"
        viewModel.uiState.value.status shouldBeEqualTo RadioPlaybackStatus.Playing
    }

    @Test
    fun `GIVEN paused station WHEN collected THEN isVisible is true`() {
        val player =
            FakeRadioPlayer(
                RadioPlayerSnapshot(
                    stationName = "BBC Radio 1",
                    status = RadioPlaybackStatus.Paused,
                ),
            )
        val viewModel = RadioPlayerBarViewModel(player).subscribed()

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.value.isVisible shouldBeEqualTo true
        viewModel.uiState.value.status shouldBeEqualTo RadioPlaybackStatus.Paused
    }

    @Test
    fun `GIVEN Play event WHEN handleEvent called THEN player play is invoked`() {
        val player = FakeRadioPlayer(RadioPlayerSnapshot(stationName = "Station", status = RadioPlaybackStatus.Paused))
        val viewModel = RadioPlayerBarViewModel(player)

        viewModel.handleEvent(RadioPlayerBarEvent.Play)

        player.playCount shouldBeEqualTo 1
    }

    @Test
    fun `GIVEN playing station and TogglePlayPause event WHEN handleEvent called THEN player pause is invoked`() {
        val player = FakeRadioPlayer(RadioPlayerSnapshot(stationName = "Station", status = RadioPlaybackStatus.Playing))
        val viewModel = RadioPlayerBarViewModel(player)

        viewModel.handleEvent(RadioPlayerBarEvent.TogglePlayPause)

        player.pauseCount shouldBeEqualTo 1
        player.playCount shouldBeEqualTo 0
    }

    @Test
    fun `GIVEN paused station and TogglePlayPause event WHEN handleEvent called THEN player play is invoked`() {
        val player = FakeRadioPlayer(RadioPlayerSnapshot(stationName = "Station", status = RadioPlaybackStatus.Paused))
        val viewModel = RadioPlayerBarViewModel(player)

        viewModel.handleEvent(RadioPlayerBarEvent.TogglePlayPause)

        player.playCount shouldBeEqualTo 1
        player.pauseCount shouldBeEqualTo 0
    }

    @Test
    fun `GIVEN Pause event WHEN handleEvent called THEN player pause is invoked`() {
        val player = FakeRadioPlayer(RadioPlayerSnapshot(stationName = "Station", status = RadioPlaybackStatus.Playing))
        val viewModel = RadioPlayerBarViewModel(player)

        viewModel.handleEvent(RadioPlayerBarEvent.Pause)

        player.pauseCount shouldBeEqualTo 1
    }

    private fun RadioPlayerBarViewModel.subscribed(): RadioPlayerBarViewModel = apply { uiCollector.launch { uiState.collect {} } }

    private class FakeRadioPlayer(
        initial: RadioPlayerSnapshot,
    ) : RadioPlayer {
        private val _snapshot = MutableStateFlow(initial)
        override val snapshot: StateFlow<RadioPlayerSnapshot> = _snapshot

        var playCount = 0
        var pauseCount = 0

        override fun connect() = Unit

        override fun playQueue(
            stations: List<RadioStation>,
            startIndex: Int,
        ) = Unit

        override fun play() {
            playCount++
        }

        override fun pause() {
            pauseCount++
        }

        override fun stop() = Unit

        override fun next() = Unit

        override fun previous() = Unit

        override fun retry() = Unit

        override fun setSleepTimer(minutes: Int?) = Unit

        override fun release() = Unit
    }
}
