package com.alxnophis.simpleradio.radioplayer.di

import com.alxnophis.simpleradio.radioplayer.playback.RadioPlayback
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlaybackImpl
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlayer
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlayerController
import com.alxnophis.simpleradio.radioplayer.ui.viewmodel.RadioPlayerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val radioPlayerModule: Module =
    module {
        single { RadioPlayerController(androidContext()) } bind RadioPlayer::class
        single<RadioPlayback> { RadioPlaybackImpl(get()) }

        viewModel { RadioPlayerViewModel(get()) }
    }
