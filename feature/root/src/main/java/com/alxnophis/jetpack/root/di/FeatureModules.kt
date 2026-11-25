package com.alxnophis.jetpack.root.di

import com.alxnophis.jetpack.home.di.homeModule
import com.alxnophis.jetpack.radio.di.radioModule
import com.alxnophis.jetpack.settings.di.settingsModule
import com.alxnophis.jetpack.storage.favorites.di.storageModule
import org.koin.core.module.Module

val featureModules: List<Module> =
    listOf(
        homeModule,
        radioModule,
        rootModule,
        settingsModule,
        storageModule,
    )
