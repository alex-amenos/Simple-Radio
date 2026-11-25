package com.alxnophis.jetpack.search.domain.usecase

import arrow.core.Either
import com.alxnophis.jetpack.search.data.model.SearchError
import com.alxnophis.jetpack.search.data.repository.SearchStationsRepository
import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation

internal class SearchStationsUseCase(
    private val repository: SearchStationsRepository,
) {
    suspend operator fun invoke(
        name: String,
        countryCode: String?,
    ): Either<SearchError, List<FavoriteStation>> = repository.searchStations(name = name.trim(), countryCode = countryCode)
}
