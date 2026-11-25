package com.alxnophis.simpleradio.core.di

import com.alxnophis.simpleradio.api.di.apiModule
import org.koin.core.module.Module

val coreModules: List<Module> =
    listOf(
        coreModule,
        apiModule,
    )
