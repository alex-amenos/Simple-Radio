package com.alxnophis.jetpack.api.radiobrowser

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import com.alxnophis.jetpack.api.radiobrowser.model.CountryApiModel
import com.alxnophis.jetpack.api.radiobrowser.model.RadioStationApiModel
import retrofit2.http.GET
import retrofit2.http.Query

interface RadioBrowserSearchService {
    @GET("json/countries")
    suspend fun getCountries(
        @Query("order") order: String = "name",
        @Query("hidebroken") hideBroken: Boolean = true,
    ): Either<CallError, List<CountryApiModel>>

    @GET("json/stations/search")
    suspend fun searchStations(
        @Query("name") name: String? = null,
        @Query("countrycode") countryCode: String? = null,
        @Query("limit") limit: Int = 100,
        @Query("order") order: String = "votes",
        @Query("reverse") reverse: Boolean = true,
        @Query("hidebroken") hideBroken: Boolean = true,
    ): Either<CallError, List<RadioStationApiModel>>
}
