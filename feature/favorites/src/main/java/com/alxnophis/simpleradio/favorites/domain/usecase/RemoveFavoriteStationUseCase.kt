package com.alxnophis.simpleradio.favorites.domain.usecase

import com.alxnophis.simpleradio.storage.favorites.repository.FavoriteStationsRepository

internal class RemoveFavoriteStationUseCase(
    private val repository: FavoriteStationsRepository,
) {
    suspend operator fun invoke(stationId: String) = repository.remove(stationId)
}
