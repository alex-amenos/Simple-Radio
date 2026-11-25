package com.alxnophis.simpleradio.api.themoviedb.di

import com.alxnophis.simpleradio.api.themoviedb.TheMovieDbMovieService
import com.alxnophis.simpleradio.api.themoviedb.TheMovieDbRetrofitFactory
import com.alxnophis.simpleradio.api.themoviedb.TheMovieDbSearchService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal val theMovieDbApiModule: Module =
    module {
        single { TheMovieDbRetrofitFactory(androidContext()) }
        single { get<TheMovieDbRetrofitFactory>().createService(TheMovieDbSearchService::class.java) }
        single { get<TheMovieDbRetrofitFactory>().createService(TheMovieDbMovieService::class.java) }
    }
