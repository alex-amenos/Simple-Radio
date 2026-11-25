package com.alxnophis.jetpack.radioplayer.ui.viewmodel

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.radioplayer.playback.RadioPlayer
import com.alxnophis.jetpack.radioplayer.playback.RadioPlayerSnapshot
import com.alxnophis.jetpack.radioplayer.ui.contract.RadioPlayerEvent
import com.alxnophis.jetpack.radioplayer.ui.contract.RadioPlayerUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
internal class RadioPlayerViewModel(
    private val player: RadioPlayer,
    initialUiState: RadioPlayerUiState = RadioPlayerUiState.initialState,
    private val clock: () -> Long = { SystemClock.elapsedRealtime() },
) : ViewModel() {
    init {
        player.connect()
    }

    private val sleepTimerRemaining: Flow<Long?> =
        player.snapshot
            .map { it.sleepTimerDeadlineMillis }
            .distinctUntilChanged()
            .flatMapLatest { deadline -> sleepTimerCountdown(deadline) }
            .catch { throwable -> Timber.e(throwable, "Error in sleep timer countdown") }

    val uiState: StateFlow<RadioPlayerUiState> =
        combine(player.snapshot, sleepTimerRemaining) { snapshot, remaining -> snapshot.toUiState(remaining) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = STOP_TIMEOUT_MILLIS),
                initialValue = initialUiState,
            )

    fun handleEvent(event: RadioPlayerEvent) {
        when (event) {
            RadioPlayerEvent.Play -> player.play()
            RadioPlayerEvent.Pause -> player.pause()
            RadioPlayerEvent.Stop -> player.stop()
            RadioPlayerEvent.Next -> player.next()
            RadioPlayerEvent.Previous -> player.previous()
            RadioPlayerEvent.Retry -> player.retry()
            is RadioPlayerEvent.SetSleepTimer -> player.setSleepTimer(event.minutes)
        }
    }

    private fun sleepTimerCountdown(deadline: Long?): Flow<Long?> =
        flow {
            if (deadline != null) {
                var remaining = deadline - clock()
                while (remaining > ZERO_MILLIS) {
                    emit(remaining)
                    delay(millisUntilDisplayChange(remaining))
                    remaining = deadline - clock()
                }
            }
            emit(null)
        }

    private fun millisUntilDisplayChange(remainingMillis: Long): Long =
        if (remainingMillis < MINUTE_MILLIS) remainingMillis else remainingMillis % MINUTE_MILLIS + 1

    private fun RadioPlayerSnapshot.toUiState(sleepTimerRemainingMillis: Long?): RadioPlayerUiState =
        RadioPlayerUiState(
            stationName = stationName,
            artworkUri = artworkUri?.toString(),
            status = status,
            canPrevious = hasPrevious,
            canNext = hasNext,
            sleepTimerRemainingMillis = sleepTimerRemainingMillis,
        )

    private companion object {
        const val ZERO_MILLIS = 0L
        const val MINUTE_MILLIS = 60_000L
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
