package com.alxnophis.jetpack.favorites.domain.usecase

import com.alxnophis.jetpack.storage.favorites.repository.FavoriteStationsRepository

internal class RemoveFavoriteStationUseCase(
    private val repository: FavoriteStationsRepository,
) {
    suspend operator fun invoke(stationId: String) = repository.remove(stationId)
}
