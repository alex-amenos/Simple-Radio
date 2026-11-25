package com.alxnophis.jetpack.search.di

import com.alxnophis.jetpack.search.data.repository.SearchStationsRepository
import com.alxnophis.jetpack.search.data.repository.SearchStationsRepositoryImpl
import com.alxnophis.jetpack.search.domain.usecase.GetCountriesUseCase
import com.alxnophis.jetpack.search.domain.usecase.ObserveFavoriteIdsUseCase
import com.alxnophis.jetpack.search.domain.usecase.SearchStationsUseCase
import com.alxnophis.jetpack.search.domain.usecase.SetStationFavoriteUseCase
import com.alxnophis.jetpack.search.ui.viewmodel.SearchViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val searchModule: Module =
    module {
        single<SearchStationsRepository> { SearchStationsRepositoryImpl(get()) }
        factory { GetCountriesUseCase(get()) }
        factory { SearchStationsUseCase(get()) }
        factory { ObserveFavoriteIdsUseCase(get()) }
        factory { SetStationFavoriteUseCase(get()) }
        viewModel { SearchViewModel(get(), get(), get(), get(), get()) }
    }
