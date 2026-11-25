package com.alxnophis.simpleradio.settings.data.repository

import arrow.core.Either
import com.alxnophis.simpleradio.settings.data.model.SettingsPreferences
import com.alxnophis.simpleradio.settings.data.model.SettingsPreferencesError
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettingsFlow(): Flow<SettingsPreferences>

    suspend fun updateThemeOption(themeOption: SettingsPreferences.ThemeOptions): Either<SettingsPreferencesError, Unit>

    suspend fun updateDefaultCountry(
        countryCode: String?,
        countryName: String?,
    ): Either<SettingsPreferencesError, Unit>
}
