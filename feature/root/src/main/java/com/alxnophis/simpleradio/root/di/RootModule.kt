package com.alxnophis.simpleradio.root.di

import com.alxnophis.simpleradio.root.ui.viewmodel.RootViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val rootModule: Module =
    module {
        viewModel { RootViewModel(get(), get()) }
    }
