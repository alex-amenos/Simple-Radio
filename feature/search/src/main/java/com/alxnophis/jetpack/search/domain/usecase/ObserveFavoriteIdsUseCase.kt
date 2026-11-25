package com.alxnophis.jetpack.search.domain.usecase

import com.alxnophis.jetpack.storage.favorites.repository.FavoriteStationsRepository
import kotlinx.coroutines.flow.Flow

internal class ObserveFavoriteIdsUseCase(
    private val repository: FavoriteStationsRepository,
) {
    operator fun invoke(): Flow<Set<String>> = repository.observeFavoriteIds()
}
