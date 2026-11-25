package com.alxnophis.simpleradio.search.domain.usecase

import arrow.core.Either
import com.alxnophis.simpleradio.search.data.model.SearchError
import com.alxnophis.simpleradio.search.data.repository.SearchStationsRepository
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation

internal class SearchStationsUseCase(
    private val repository: SearchStationsRepository,
) {
    suspend operator fun invoke(
        name: String,
        countryCode: String?,
    ): Either<SearchError, List<FavoriteStation>> = repository.searchStations(name = name.trim(), countryCode = countryCode)
}
