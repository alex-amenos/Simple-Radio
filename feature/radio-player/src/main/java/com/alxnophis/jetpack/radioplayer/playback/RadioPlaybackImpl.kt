package com.alxnophis.jetpack.radioplayer.playback

import com.alxnophis.jetpack.radioplayer.data.mapper.toRadioStation
import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation

internal class RadioPlaybackImpl(
    private val player: RadioPlayer,
) : RadioPlayback {
    override fun play(
        stations: List<FavoriteStation>,
        startIndex: Int,
    ) {
        if (startIndex !in stations.indices) return
        player.connect()
        player.playQueue(stations.map { it.toRadioStation() }, startIndex)
    }
}
