package com.alxnophis.simpleradio.api.radiobrowser.di

import com.alxnophis.simpleradio.api.radiobrowser.RadioBrowserRetrofitFactory
import com.alxnophis.simpleradio.api.radiobrowser.RadioBrowserSearchService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal val radioBrowserApiModule: Module =
    module {
        single { RadioBrowserRetrofitFactory(androidContext()) }
        single { get<RadioBrowserRetrofitFactory>().createService(RadioBrowserSearchService::class.java) }
    }
