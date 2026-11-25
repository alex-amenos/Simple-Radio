package com.alxnophis.simpleradio.home.di

import com.alxnophis.simpleradio.home.ui.viewmodel.HomeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule: Module =
    module {
        viewModel { HomeViewModel() }
    }
