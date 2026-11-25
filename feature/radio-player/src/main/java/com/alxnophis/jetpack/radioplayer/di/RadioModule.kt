package com.alxnophis.jetpack.radioplayer.di

import com.alxnophis.jetpack.radioplayer.data.repository.RadioStationsRepositoryImpl
import com.alxnophis.jetpack.radioplayer.data.repository.RadioStationsRepository
import com.alxnophis.jetpack.radioplayer.playback.RadioPlayback
import com.alxnophis.jetpack.radioplayer.playback.RadioPlaybackImpl
import com.alxnophis.jetpack.radioplayer.playback.RadioPlayer
import com.alxnophis.jetpack.radioplayer.playback.RadioPlayerController
import com.alxnophis.jetpack.radioplayer.ui.viewmodel.RadioPlayerViewModel
import com.alxnophis.jetpack.radioplayer.ui.viewmodel.RadioStationListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val radioPlayerModule: Module =
    module {
        single { RadioPlayerController(androidContext()) } bind RadioPlayer::class
        single<RadioPlayback> { RadioPlaybackImpl(get()) }
        single<RadioStationsRepository> { RadioStationsRepositoryImpl(get()) }

        viewModel { RadioStationListViewModel(get(), get()) }
        viewModel { RadioPlayerViewModel(get()) }
    }
