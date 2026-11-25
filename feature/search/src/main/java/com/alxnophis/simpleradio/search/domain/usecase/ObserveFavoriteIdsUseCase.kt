package com.alxnophis.simpleradio.search.domain.usecase

import com.alxnophis.simpleradio.storage.favorites.repository.FavoriteStationsRepository
import kotlinx.coroutines.flow.Flow

internal class ObserveFavoriteIdsUseCase(
    private val repository: FavoriteStationsRepository,
) {
    operator fun invoke(): Flow<Set<String>> = repository.observeFavoriteIds()
}
