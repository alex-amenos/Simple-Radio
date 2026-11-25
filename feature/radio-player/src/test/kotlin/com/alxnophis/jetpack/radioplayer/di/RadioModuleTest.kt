package com.alxnophis.jetpack.radioplayer.di

import android.content.Context
import com.alxnophis.jetpack.api.radiobrowser.RadioBrowserStationsService
import org.junit.jupiter.api.Test
import org.koin.test.verify.verify

internal class RadioModuleTest {
    @Test
    fun `GIVEN radioPlayerModule WHEN verified THEN all definitions resolve`() {
        radioPlayerModule.verify(
            extraTypes = listOf(Context::class, RadioBrowserStationsService::class),
        )
    }
}
