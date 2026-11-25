package com.alxnophis.simpleradio.storage.favorites.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_stations")
internal data class FavoriteStationEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "stream_url") val streamUrl: String,
    @ColumnInfo(name = "favicon_url") val faviconUrl: String?,
    @ColumnInfo(name = "codec") val codec: String?,
    @ColumnInfo(name = "bitrate") val bitrate: Int?,
    @ColumnInfo(name = "country") val country: String?,
    @ColumnInfo(name = "added_at") val addedAt: Long,
)
