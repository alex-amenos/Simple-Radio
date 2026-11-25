package com.alxnophis.simpleradio.countryselector.data.repository

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import com.alxnophis.simpleradio.countryselector.data.model.Country

interface CountrySelectorRepository {
    suspend fun getCountries(): Either<CallError, List<Country>>
}
