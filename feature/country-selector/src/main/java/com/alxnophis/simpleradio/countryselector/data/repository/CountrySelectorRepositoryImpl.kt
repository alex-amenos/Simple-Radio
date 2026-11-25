package com.alxnophis.simpleradio.countryselector.data.repository

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import com.alxnophis.simpleradio.api.radiobrowser.RadioBrowserSearchService
import com.alxnophis.simpleradio.api.radiobrowser.model.CountryApiModel
import com.alxnophis.simpleradio.countryselector.data.model.Country

internal class CountrySelectorRepositoryImpl(
    private val service: RadioBrowserSearchService,
) : CountrySelectorRepository {
    override suspend fun getCountries(): Either<CallError, List<Country>> =
        service
            .getCountries()
            .map { countries ->
                countries
                    .mapNotNull { it.toCountryOrNull() }
                    .distinctBy { it.code }
                    .sortedBy { it.name.lowercase() }
            }

    private fun CountryApiModel.toCountryOrNull(): Country? {
        val code = isoCode?.takeIf { it.isNotBlank() } ?: return null
        if (name.isBlank() || stationCount <= 0) return null
        return Country(code = code, name = name.trim())
    }
}
