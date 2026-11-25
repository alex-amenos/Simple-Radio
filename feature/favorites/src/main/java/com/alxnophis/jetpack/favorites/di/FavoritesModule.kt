package com.alxnophis.jetpack.favorites.di

import com.alxnophis.jetpack.favorites.domain.usecase.ObserveFavoriteStationsUseCase
import com.alxnophis.jetpack.favorites.domain.usecase.RemoveFavoriteStationUseCase
import com.alxnophis.jetpack.favorites.ui.viewmodel.FavoritesViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val favoritesModule: Module =
    module {
        factory { ObserveFavoriteStationsUseCase(get()) }
        factory { RemoveFavoriteStationUseCase(get()) }
        viewModel { FavoritesViewModel(get(), get()) }
    }
