package com.alxnophis.jetpack.radioplayer.data.mapper

import com.alxnophis.jetpack.radioplayer.data.model.RadioStation
import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation

internal fun FavoriteStation.toRadioStation(): RadioStation =
    RadioStation(
        id = id,
        name = name,
        streamUrl = streamUrl,
        faviconUrl = faviconUrl,
        codec = codec,
        bitrate = bitrate,
    )
