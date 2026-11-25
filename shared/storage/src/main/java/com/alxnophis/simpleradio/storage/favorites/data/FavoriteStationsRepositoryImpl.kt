package com.alxnophis.simpleradio.storage.favorites.data

import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation
import com.alxnophis.simpleradio.storage.favorites.repository.FavoriteStationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class FavoriteStationsRepositoryImpl(
    private val dao: FavoriteStationDao,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis,
) : FavoriteStationsRepository {
    override fun observeFavorites(): Flow<List<FavoriteStation>> = dao.observeAll().map { entities -> entities.map { it.toModel() } }

    override fun observeFavoriteIds(): Flow<Set<String>> = dao.observeIds().map { it.toSet() }

    override suspend fun add(station: FavoriteStation) {
        dao.insert(station.toEntity(addedAt = currentTimeMillis()))
    }

    override suspend fun remove(stationId: String) {
        dao.delete(stationId)
    }
}
