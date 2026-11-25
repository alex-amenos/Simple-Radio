package com.alxnophis.simpleradio.radioplayer.playback

import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation

/**
 * Public entry point to start playing stations coming from other features (favorites, search).
 */
interface RadioPlayback {
    fun play(
        stations: List<FavoriteStation>,
        startIndex: Int,
    )
}
