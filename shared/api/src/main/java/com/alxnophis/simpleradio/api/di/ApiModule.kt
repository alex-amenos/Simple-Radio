package com.alxnophis.simpleradio.api.di

import com.alxnophis.simpleradio.api.jsonplaceholder.di.jsonPlaceholderApiModule
import com.alxnophis.simpleradio.api.radiobrowser.di.radioBrowserApiModule
import com.alxnophis.simpleradio.api.themoviedb.di.theMovieDbApiModule
import org.koin.core.module.Module
import org.koin.dsl.module

val apiModule: Module =
    module {
        includes(jsonPlaceholderApiModule)
        includes(theMovieDbApiModule)
        includes(radioBrowserApiModule)
    }
