package com.alxnophis.jetpack.search.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.search.data.model.Country
import com.alxnophis.jetpack.search.data.model.SearchError
import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation

internal interface SearchStationsRepository {
    suspend fun getCountries(): Either<SearchError, List<Country>>

    suspend fun searchStations(
        name: String,
        countryCode: String?,
    ): Either<SearchError, List<FavoriteStation>>
}
