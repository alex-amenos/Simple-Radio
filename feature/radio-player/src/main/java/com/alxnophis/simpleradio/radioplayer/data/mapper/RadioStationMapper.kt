package com.alxnophis.simpleradio.radioplayer.data.mapper

import com.alxnophis.simpleradio.radioplayer.data.model.RadioStation
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation

internal fun FavoriteStation.toRadioStation(): RadioStation =
    RadioStation(
        id = id,
        name = name,
        streamUrl = streamUrl,
        faviconUrl = faviconUrl,
        codec = codec,
        bitrate = bitrate,
    )
