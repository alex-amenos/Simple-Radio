package com.alxnophis.simpleradio.countryselector.data.repository

import arrow.core.Either
import com.alxnophis.simpleradio.api.radiobrowser.RadioBrowserSearchService
import com.alxnophis.simpleradio.api.radiobrowser.model.CountryApiModel
import com.alxnophis.simpleradio.countryselector.data.model.Country
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

internal class CountrySelectorRepositoryImplTest {
    @Test
    fun `GIVEN api returns countries WHEN getCountries THEN maps filters and sorts distinct countries`() =
        runTest {
            val apiCountries =
                listOf(
                    CountryApiModel(name = "Spain", isoCode = "ES", stationCount = 10),
                    CountryApiModel(name = "spain duplicate", isoCode = "ES", stationCount = 5),
                    CountryApiModel(name = "Andorra", isoCode = "AD", stationCount = 2),
                    CountryApiModel(name = "Empty Code", isoCode = "", stationCount = 1),
                    CountryApiModel(name = "Zero Stations", isoCode = "ZZ", stationCount = 0),
                    CountryApiModel(name = "   ", isoCode = "BL", stationCount = 4),
                )
            val service =
                mock<RadioBrowserSearchService> {
                    onBlocking { getCountries() } doReturn Either.Right(apiCountries)
                }
            val repository = CountrySelectorRepositoryImpl(service)

            val result = repository.getCountries()

            result shouldBeEqualTo
                Either.Right(
                    listOf(
                        Country(code = "AD", name = "Andorra"),
                        Country(code = "ES", name = "Spain"),
                    ),
                )
        }
}
