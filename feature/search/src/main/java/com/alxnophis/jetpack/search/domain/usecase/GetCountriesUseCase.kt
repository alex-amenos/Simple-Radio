package com.alxnophis.jetpack.search.domain.usecase

import arrow.core.Either
import com.alxnophis.jetpack.search.data.model.Country
import com.alxnophis.jetpack.search.data.model.SearchError
import com.alxnophis.jetpack.search.data.repository.SearchStationsRepository

internal class GetCountriesUseCase(
    private val repository: SearchStationsRepository,
) {
    suspend operator fun invoke(): Either<SearchError, List<Country>> = repository.getCountries()
}
