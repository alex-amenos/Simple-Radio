package com.alxnophis.simpleradio.settings.di

import com.alxnophis.simpleradio.settings.data.model.SettingsPreferencesSerializer
import com.alxnophis.simpleradio.settings.data.repository.SettingsRepository
import com.alxnophis.simpleradio.settings.data.repository.SettingsRepositoryImpl
import com.alxnophis.simpleradio.settings.ui.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule: Module =
    module {
        single { SettingsPreferencesSerializer() }
        single<SettingsRepository> { SettingsRepositoryImpl(androidContext(), get()) }
        viewModel { SettingsViewModel(get()) }
    }
