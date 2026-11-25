package com.alxnophis.simpleradio.storage.favorites.repository

import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation
import kotlinx.coroutines.flow.Flow

interface FavoriteStationsRepository {
    fun observeFavorites(): Flow<List<FavoriteStation>>

    fun observeFavoriteIds(): Flow<Set<String>>

    suspend fun add(station: FavoriteStation)

    suspend fun remove(stationId: String)
}
