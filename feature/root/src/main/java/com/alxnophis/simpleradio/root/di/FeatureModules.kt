package com.alxnophis.simpleradio.root.di

import com.alxnophis.simpleradio.countryselector.di.countrySelectorModule
import com.alxnophis.simpleradio.favorites.di.favoritesModule
import com.alxnophis.simpleradio.home.di.homeModule
import com.alxnophis.simpleradio.radioplayer.di.radioPlayerModule
import com.alxnophis.simpleradio.search.di.searchModule
import com.alxnophis.simpleradio.settings.di.settingsModule
import com.alxnophis.simpleradio.storage.favorites.di.storageModule
import org.koin.core.module.Module

val featureModules: List<Module> =
    listOf(
        countrySelectorModule,
        favoritesModule,
        homeModule,
        radioPlayerModule,
        rootModule,
        searchModule,
        settingsModule,
        storageModule,
    )
