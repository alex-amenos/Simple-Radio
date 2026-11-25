package com.alxnophis.jetpack.search.data.mapper

import com.alxnophis.jetpack.api.radiobrowser.model.CountryApiModel
import com.alxnophis.jetpack.api.radiobrowser.model.RadioStationApiModel
import com.alxnophis.jetpack.search.data.model.Country
import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation

private const val HTTPS_SCHEME = "https"

internal fun RadioStationApiModel.toStationOrNull(): FavoriteStation? {
    val url = urlResolved?.takeIf { it.isNotBlank() } ?: return null
    if (!url.startsWith("$HTTPS_SCHEME://", ignoreCase = true)) return null
    return FavoriteStation(
        id = stationUuid,
        name = name.trim(),
        streamUrl = url,
        faviconUrl = favicon?.takeIf { it.isNotBlank() && it.startsWith("$HTTPS_SCHEME://", ignoreCase = true) },
        codec = codec?.takeIf { it.isNotBlank() },
        bitrate = bitrate,
        country = country?.takeIf { it.isNotBlank() },
    )
}

internal fun CountryApiModel.toCountryOrNull(): Country? {
    val code = isoCode?.takeIf { it.isNotBlank() } ?: return null
    if (name.isBlank() || stationCount <= 0) return null
    return Country(code = code, name = name.trim())
}
