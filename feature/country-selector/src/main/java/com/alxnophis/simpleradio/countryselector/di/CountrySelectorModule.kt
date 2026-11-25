package com.alxnophis.simpleradio.countryselector.di

import com.alxnophis.simpleradio.countryselector.data.repository.CountrySelectorRepository
import com.alxnophis.simpleradio.countryselector.data.repository.CountrySelectorRepositoryImpl
import com.alxnophis.simpleradio.countryselector.ui.viewmodel.CountrySelectorViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val countrySelectorModule: Module =
    module {
        single<CountrySelectorRepository> { CountrySelectorRepositoryImpl(get()) }
        viewModel { params -> CountrySelectorViewModel(get(), params.getOrNull()) }
    }
