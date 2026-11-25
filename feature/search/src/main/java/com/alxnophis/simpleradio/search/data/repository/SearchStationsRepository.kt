package com.alxnophis.simpleradio.search.data.repository

import arrow.core.Either
import com.alxnophis.simpleradio.search.data.model.SearchError
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation

internal interface SearchStationsRepository {
    suspend fun searchStations(
        name: String,
        countryCode: String?,
    ): Either<SearchError, List<FavoriteStation>>
}
