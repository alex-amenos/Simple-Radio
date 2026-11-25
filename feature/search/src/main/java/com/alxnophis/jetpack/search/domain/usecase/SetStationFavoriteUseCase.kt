package com.alxnophis.jetpack.search.domain.usecase

import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation
import com.alxnophis.jetpack.storage.favorites.repository.FavoriteStationsRepository

internal class SetStationFavoriteUseCase(
    private val repository: FavoriteStationsRepository,
) {
    suspend operator fun invoke(
        station: FavoriteStation,
        favorite: Boolean,
    ) {
        if (favorite) repository.add(station) else repository.remove(station.id)
    }
}
