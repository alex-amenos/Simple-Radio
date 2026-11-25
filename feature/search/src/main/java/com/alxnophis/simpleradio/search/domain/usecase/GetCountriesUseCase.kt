package com.alxnophis.simpleradio.search.domain.usecase

import arrow.core.Either
import com.alxnophis.simpleradio.search.data.model.Country
import com.alxnophis.simpleradio.search.data.model.SearchError
import com.alxnophis.simpleradio.search.data.repository.SearchStationsRepository

internal class GetCountriesUseCase(
    private val repository: SearchStationsRepository,
) {
    suspend operator fun invoke(): Either<SearchError, List<Country>> = repository.getCountries()
}
