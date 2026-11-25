package com.alxnophis.jetpack.settings.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.settings.data.model.SettingsPreferences
import com.alxnophis.jetpack.settings.data.model.SettingsPreferencesError
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettingsFlow(): Flow<SettingsPreferences>

    suspend fun updateThemeOption(themeOption: SettingsPreferences.ThemeOptions): Either<SettingsPreferencesError, Unit>
}
