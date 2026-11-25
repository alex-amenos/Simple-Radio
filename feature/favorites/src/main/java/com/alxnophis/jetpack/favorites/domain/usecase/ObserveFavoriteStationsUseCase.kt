package com.alxnophis.jetpack.favorites.domain.usecase

import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation
import com.alxnophis.jetpack.storage.favorites.repository.FavoriteStationsRepository
import kotlinx.coroutines.flow.Flow

internal class ObserveFavoriteStationsUseCase(
    private val repository: FavoriteStationsRepository,
) {
    operator fun invoke(): Flow<List<FavoriteStation>> = repository.observeFavorites()
}
