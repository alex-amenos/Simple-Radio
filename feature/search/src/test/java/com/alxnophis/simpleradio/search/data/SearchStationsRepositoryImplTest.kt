package com.alxnophis.simpleradio.search.data

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.retrofit.adapter.either.networkhandling.CallError
import com.alxnophis.simpleradio.api.radiobrowser.RadioBrowserSearchService
import com.alxnophis.simpleradio.api.radiobrowser.model.CountryApiModel
import com.alxnophis.simpleradio.api.radiobrowser.model.RadioStationApiModel
import com.alxnophis.simpleradio.api.testing.CallErrorMother
import com.alxnophis.simpleradio.search.data.model.SearchError
import com.alxnophis.simpleradio.search.data.repository.SearchStationsRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

internal class SearchStationsRepositoryImplTest {
    @Test
    fun `GIVEN stations with valid and invalid urls WHEN searching THEN only valid unique stations are returned`() =
        runTest {
            val service =
                FakeSearchService(
                    stations =
                        listOf(
                            apiModel("1", "https://ok.example.com/a.mp3"),
                            apiModel("2", "https://ok.example.com/a.mp3"),
                            apiModel("3", null),
                            apiModel("4", "http://insecure.example.com/b.mp3"),
                        ).right(),
                )

            val result = SearchStationsRepositoryImpl(service).searchStations(name = "radio", countryCode = null)

            result.map { stations -> stations.map { it.id } } shouldBeEqualTo listOf("1").right()
        }

    @Test
    fun `GIVEN a blank name and a country WHEN searching THEN name is not sent and country code is`() =
        runTest {
            val service = FakeSearchService(stations = emptyList<RadioStationApiModel>().right())

            SearchStationsRepositoryImpl(service).searchStations(name = "  ", countryCode = "ES")

            service.lastName shouldBeEqualTo null
            service.lastCountryCode shouldBeEqualTo "ES"
        }

    @Test
    fun `GIVEN no connectivity WHEN searching THEN returns NetworkUnavailable`() =
        runTest {
            val service = FakeSearchService(stations = Either.Left(CallErrorMother.noConnectivityError()))

            val result = SearchStationsRepositoryImpl(service).searchStations(name = "radio", countryCode = null)

            result shouldBeEqualTo SearchError.NetworkUnavailable.left()
        }

    @Test
    fun `GIVEN a server error WHEN searching THEN returns ServerError`() =
        runTest {
            val service = FakeSearchService(stations = Either.Left(CallErrorMother.httpError(500)))

            val result = SearchStationsRepositoryImpl(service).searchStations(name = "radio", countryCode = null)

            result shouldBeEqualTo SearchError.ServerError.left()
        }

    private fun apiModel(
        id: String,
        url: String?,
    ) = RadioStationApiModel(
        stationUuid = id,
        name = "Station $id",
        urlResolved = url,
        favicon = null,
        codec = "MP3",
        bitrate = 128,
        country = "Spain",
    )

    private class FakeSearchService(
        var stations: Either<CallError, List<RadioStationApiModel>> = emptyList<RadioStationApiModel>().right(),
    ) : RadioBrowserSearchService {
        var lastName: String? = null
        var lastCountryCode: String? = null

        override suspend fun getCountries(
            order: String,
            hideBroken: Boolean,
        ): Either<CallError, List<CountryApiModel>> = emptyList<CountryApiModel>().right()

        override suspend fun searchStations(
            name: String?,
            countryCode: String?,
            limit: Int,
            order: String,
            reverse: Boolean,
            hideBroken: Boolean,
        ): Either<CallError, List<RadioStationApiModel>> {
            lastName = name
            lastCountryCode = countryCode
            return stations
        }
    }
}
