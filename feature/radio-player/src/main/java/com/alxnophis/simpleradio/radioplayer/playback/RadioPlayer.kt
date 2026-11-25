package com.alxnophis.simpleradio.radioplayer.playback

import com.alxnophis.simpleradio.radioplayer.data.model.RadioStation
import kotlinx.coroutines.flow.StateFlow

internal interface RadioPlayer {
    val snapshot: StateFlow<RadioPlayerSnapshot>

    fun connect()

    fun playQueue(
        stations: List<RadioStation>,
        startIndex: Int,
    )

    fun play()

    fun pause()

    fun stop()

    fun next()

    fun previous()

    fun retry()

    fun setSleepTimer(minutes: Int?)

    fun release()
}
