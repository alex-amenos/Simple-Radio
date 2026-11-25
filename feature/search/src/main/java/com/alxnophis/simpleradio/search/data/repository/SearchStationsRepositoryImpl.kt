package com.alxnophis.simpleradio.search.data.repository

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import arrow.retrofit.adapter.either.networkhandling.HttpError
import arrow.retrofit.adapter.either.networkhandling.IOError
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import com.alxnophis.simpleradio.api.exception.NoConnectivityException
import com.alxnophis.simpleradio.api.radiobrowser.RadioBrowserSearchService
import com.alxnophis.simpleradio.search.data.mapper.toStationOrNull
import com.alxnophis.simpleradio.search.data.model.SearchError
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation
import timber.log.Timber

internal class SearchStationsRepositoryImpl(
    private val service: RadioBrowserSearchService,
) : SearchStationsRepository {
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
