package com.alxnophis.jetpack.search.data.repository

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import arrow.retrofit.adapter.either.networkhandling.HttpError
import arrow.retrofit.adapter.either.networkhandling.IOError
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import com.alxnophis.jetpack.api.exception.NoConnectivityException
import com.alxnophis.jetpack.api.radiobrowser.RadioBrowserSearchService
import com.alxnophis.jetpack.search.data.mapper.toCountryOrNull
import com.alxnophis.jetpack.search.data.mapper.toStationOrNull
import com.alxnophis.jetpack.search.data.model.Country
import com.alxnophis.jetpack.search.data.model.SearchError
import com.alxnophis.jetpack.storage.favorites.model.FavoriteStation
import timber.log.Timber

internal class SearchStationsRepositoryImpl(
    private val service: RadioBrowserSearchService,
) : SearchStationsRepository {
    override suspend fun getCountries(): Either<SearchError, List<Country>> =
        service
            .getCountries()
            .map { countries ->
                countries
                    .mapNotNull { it.toCountryOrNull() }
                    .distinctBy { it.code }
                    .sortedBy { it.name.lowercase() }
            }.mapLeft { error ->
                Timber.e("GET countries error: $error")
                error.toSearchError()
            }

    override suspend fun searchStations(
        name: String,
        countryCode: String?,
    ): Either<SearchError, List<FavoriteStation>> =
        service
            .searchStations(
                name = name.takeIf { it.isNotBlank() },
                countryCode = countryCode,
            ).map { stations ->
                stations
                    .mapNotNull { it.toStationOrNull() }
                    .distinctBy { it.streamUrl }
            }.mapLeft { error ->
                Timber.e("Search stations error: $error")
                error.toSearchError()
            }

    private fun CallError.toSearchError(): SearchError =
        when (this) {
            is IOError -> if (cause is NoConnectivityException) SearchError.NetworkUnavailable else SearchError.ServerError
            is UnexpectedCallError -> SearchError.ServerError
            is HttpError -> SearchError.ServerError
        }
}
