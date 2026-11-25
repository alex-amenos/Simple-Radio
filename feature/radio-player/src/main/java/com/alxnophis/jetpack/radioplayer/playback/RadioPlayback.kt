package com.alxnophis.jetpack.radioplayer.playback

import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation

/**
 * Public entry point to start playing stations coming from other features (favorites, search).
 */
interface RadioPlayback {
    fun play(
        stations: List<FavoriteStation>,
        startIndex: Int,
    )
}
