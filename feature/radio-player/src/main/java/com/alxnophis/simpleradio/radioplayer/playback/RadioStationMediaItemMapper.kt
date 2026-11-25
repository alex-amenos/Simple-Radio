package com.alxnophis.simpleradio.radioplayer.playback

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.alxnophis.simpleradio.radioplayer.data.model.RadioStation

private const val ARTIST = "Radio Browser"

internal fun RadioStation.toMediaItem(): MediaItem =
    MediaItem
        .Builder()
        .setMediaId(id)
        .setUri(streamUrl)
        .setMediaMetadata(
            MediaMetadata
                .Builder()
                .setTitle(name)
                .setArtist(ARTIST)
                .setArtworkUri(faviconUrl?.let(Uri::parse))
                .build(),
        ).setLiveConfiguration(
            MediaItem.LiveConfiguration
                .Builder()
                .setMaxPlaybackSpeed(1.0f)
                .build(),
        ).build()
