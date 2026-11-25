package com.alxnophis.simpleradio.core.di

import com.alxnophis.simpleradio.core.base.provider.BaseRandomProvider
import com.alxnophis.simpleradio.core.connectivity.ConnectivityNetworkMonitor
import com.alxnophis.simpleradio.core.connectivity.NetworkMonitor
import com.alxnophis.simpleradio.core.ui.formatter.BaseDateFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

@Suppress("InlineCoroutineScope")
val coreModule: Module =
    module {
        factory { BaseDateFormatter() }
        factory { BaseRandomProvider() }

        // Application-level CoroutineScope that lives for the entire app lifecycle
        // Uses IO dispatcher since background work (network/database) is I/O-bound
        single(named("applicationScope")) {
            CoroutineScope(SupervisorJob() + Dispatchers.IO)
        }

        single<NetworkMonitor> { ConnectivityNetworkMonitor(androidContext()) }
    }
