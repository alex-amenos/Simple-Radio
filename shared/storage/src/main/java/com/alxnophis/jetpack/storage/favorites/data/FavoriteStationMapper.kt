package com.alxnophis.jetpack.storage.favorites.data

import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation

internal fun FavoriteStation.toEntity(addedAt: Long): FavoriteStationEntity =
    FavoriteStationEntity(
        id = id,
        name = name,
        streamUrl = streamUrl,
        faviconUrl = faviconUrl,
        codec = codec,
        bitrate = bitrate,
        country = country,
        addedAt = addedAt,
    )

internal fun FavoriteStationEntity.toModel(): FavoriteStation =
    FavoriteStation(
        id = id,
        name = name,
        streamUrl = streamUrl,
        faviconUrl = faviconUrl,
        codec = codec,
        bitrate = bitrate,
        country = country,
    )
