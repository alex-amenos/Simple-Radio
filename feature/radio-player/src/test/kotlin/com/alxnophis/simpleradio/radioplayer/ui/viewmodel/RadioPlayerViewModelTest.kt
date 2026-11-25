package com.alxnophis.simpleradio.radioplayer.ui.viewmodel

import com.alxnophis.simpleradio.radioplayer.data.model.RadioStation
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlaybackStatus
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlayerSnapshot
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlayer
import com.alxnophis.simpleradio.radioplayer.ui.contract.RadioPlayerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class RadioPlayerViewModelTest {
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
    fun `GIVEN first station WHEN snapshot is collected THEN canPrevious is false`() {
        val player = FakeRadioPlayer(RadioPlayerSnapshot(stationName = "Cadena SER", hasPrevious = false, hasNext = true))

        val viewModel = RadioPlayerViewModel(player).subscribed()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.value.canPrevious shouldBeEqualTo false
        viewModel.uiState.value.canNext shouldBeEqualTo true
    }

    @Test
    fun `GIVEN last station WHEN snapshot is collected THEN canNext is false`() {
        val player = FakeRadioPlayer(RadioPlayerSnapshot(stationName = "Cadena SER", hasPrevious = true, hasNext = false))

        val viewModel = RadioPlayerViewModel(player).subscribed()
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.value.canNext shouldBeEqualTo false
    }

    @Test
    fun `GIVEN error status WHEN Retry is handled THEN player retry is called`() {
        val player = FakeRadioPlayer(RadioPlayerSnapshot(status = RadioPlaybackStatus.Error))
        val viewModel = RadioPlayerViewModel(player)

        viewModel.handleEvent(RadioPlayerEvent.Retry)

        player.retryCount shouldBeEqualTo 1
    }

    @Test
    fun `GIVEN playing WHEN Stop is handled THEN player stop is called`() {
        val player = FakeRadioPlayer(RadioPlayerSnapshot(status = RadioPlaybackStatus.Playing))
        val viewModel = RadioPlayerViewModel(player)

        viewModel.handleEvent(RadioPlayerEvent.Stop)

        player.stopCount shouldBeEqualTo 1
    }

    @Test
    fun `GIVEN a sleep deadline WHEN time elapses THEN countdown is updated only when the displayed minute changes`() {
        val player = FakeRadioPlayer(RadioPlayerSnapshot(sleepTimerDeadlineMillis = 180_000L))
        val viewModel = RadioPlayerViewModel(player, clock = { dispatcher.scheduler.currentTime }).subscribed()

        dispatcher.scheduler.runCurrent()
        viewModel.uiState.value.sleepTimerRemainingMillis shouldBeEqualTo 180_000L

        dispatcher.scheduler.advanceTimeBy(1L)
        dispatcher.scheduler.runCurrent()
        viewModel.uiState.value.sleepTimerRemainingMillis shouldBeEqualTo 179_999L

        dispatcher.scheduler.advanceTimeBy(59_999L)
        dispatcher.scheduler.runCurrent()
        viewModel.uiState.value.sleepTimerRemainingMillis shouldBeEqualTo 179_999L

        dispatcher.scheduler.advanceTimeBy(1L)
        dispatcher.scheduler.runCurrent()
        viewModel.uiState.value.sleepTimerRemainingMillis shouldBeEqualTo 119_999L
    }

    @Test
    fun `GIVEN a sleep deadline WHEN the deadline passes THEN remaining countdown is null`() {
        val player = FakeRadioPlayer(RadioPlayerSnapshot(sleepTimerDeadlineMillis = 3_000L))
        val viewModel = RadioPlayerViewModel(player, clock = { dispatcher.scheduler.currentTime }).subscribed()

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.value.sleepTimerRemainingMillis
            .shouldBeNull()
    }

    @Test
    fun `GIVEN no sleep deadline WHEN snapshot is collected THEN remaining countdown is null`() {
        val player = FakeRadioPlayer(RadioPlayerSnapshot(stationName = "Cadena SER"))
        val viewModel = RadioPlayerViewModel(player, clock = { dispatcher.scheduler.currentTime }).subscribed()

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.value.sleepTimerRemainingMillis
            .shouldBeNull()
    }

    @Test
    fun `GIVEN no subscribers WHEN view model is created THEN snapshot is not observed`() {
        val player = FakeRadioPlayer(RadioPlayerSnapshot(stationName = "Cadena SER"))

        RadioPlayerViewModel(player)
        dispatcher.scheduler.advanceUntilIdle()

        player.snapshotSubscribers shouldBeEqualTo 0
    }

    // uiState is shared WhileSubscribed, so the UI collector is simulated to keep it updated.
    private fun RadioPlayerViewModel.subscribed(): RadioPlayerViewModel = apply { uiCollector.launch { uiState.collect {} } }

    private class FakeRadioPlayer(
        initial: RadioPlayerSnapshot,
    ) : RadioPlayer {
        private val _snapshot = MutableStateFlow(initial)
        override val snapshot: StateFlow<RadioPlayerSnapshot> = _snapshot
        val snapshotSubscribers: Int
            get() = _snapshot.subscriptionCount.value
        var retryCount = 0
        var stopCount = 0

        override fun connect() = Unit

        override fun playQueue(
            stations: List<RadioStation>,
            startIndex: Int,
        ) = Unit

        override fun play() = Unit

        override fun pause() = Unit

        override fun stop() {
            stopCount++
        }

        override fun next() = Unit

        override fun previous() = Unit

        override fun retry() {
            retryCount++
        }

        override fun setSleepTimer(minutes: Int?) = Unit

        override fun release() = Unit
    }
}
