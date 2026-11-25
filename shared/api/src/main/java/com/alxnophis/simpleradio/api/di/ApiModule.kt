package com.alxnophis.simpleradio.api.di

import com.alxnophis.simpleradio.api.radiobrowser.di.radioBrowserApiModule
import org.koin.core.module.Module
import org.koin.dsl.module

val apiModule: Module =
    module {
        includes(radioBrowserApiModule)
    }
