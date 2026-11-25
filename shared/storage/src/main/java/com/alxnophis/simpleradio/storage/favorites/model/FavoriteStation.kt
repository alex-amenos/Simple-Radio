package com.alxnophis.simpleradio.storage.favorites.model

data class FavoriteStation(
    val id: String,
    val name: String,
    val streamUrl: String,
    val faviconUrl: String?,
    val codec: String?,
    val bitrate: Int?,
    val country: String?,
)
