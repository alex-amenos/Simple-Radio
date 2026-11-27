package com.alxnophis.simpleradio.settings.data.repository

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.dataStore
import arrow.core.Either
import arrow.core.Either.Companion.catch
import com.alxnophis.simpleradio.core.ui.theme.AppNightMode
import com.alxnophis.simpleradio.core.ui.theme.AppNightModeManager
import com.alxnophis.simpleradio.settings.data.model.SettingsPreferences
import com.alxnophis.simpleradio.settings.data.model.SettingsPreferencesError
import com.alxnophis.simpleradio.settings.data.model.SettingsPreferencesSerializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

internal typealias UpdatedSuccessfully = Unit

internal class SettingsRepositoryImpl(
    private val context: Context,
    private val serializer: SettingsPreferencesSerializer = SettingsPreferencesSerializer(),
) : SettingsRepository {
    private val Context.dataStore: DataStore<SettingsPreferences> by dataStore(
        fileName = SETTINGS_PREFERENCES_FILE_NAME,
        serializer = serializer,
        produceMigrations = {
            listOf(
                object : DataMigration<SettingsPreferences> {
                    override suspend fun shouldMigrate(currentData: SettingsPreferences): Boolean = serializer.hasLegacyEncryptedData.get()

                    override suspend fun migrate(currentData: SettingsPreferences): SettingsPreferences {
                        serializer.hasLegacyEncryptedData.set(false)
                        return currentData
                    }

                    override suspend fun cleanUp() {
                        serializer.hasLegacyEncryptedData.set(false)
                    }
                },
            )
        },
    )

    private val settingsDataStore: DataStore<SettingsPreferences>
        get() = context.dataStore

    private val settingsDataStoreData: Flow<SettingsPreferences>
        get() = context.dataStore.data

    override fun getSettingsFlow(): Flow<SettingsPreferences> =
        settingsDataStoreData.onEach { AppNightModeManager.save(context, it.themeOption.toAppNightMode()) }

    private suspend inline fun updatePreference(
        crossinline update: (SettingsPreferences) -> SettingsPreferences,
    ): Either<SettingsPreferencesError, Unit> =
        catch {
            settingsDataStore.updateData { update(it) }
        }.map { UpdatedSuccessfully }
            .mapLeft { error ->
                when (error) {
                    is IOException -> SettingsPreferencesError.Disk
                    else -> SettingsPreferencesError.Unexpected
                }
            }

    override suspend fun updateThemeOption(themeOption: SettingsPreferences.ThemeOptions): Either<SettingsPreferencesError, Unit> =
        updatePreference {
            it.copy(themeOption = themeOption)
        }.onRight {
            val nightMode = themeOption.toAppNightMode()
            AppNightModeManager.save(context, nightMode)
            AppNightModeManager.apply(context, nightMode)
        }

    override suspend fun updateDefaultCountry(
        countryCode: String?,
        countryName: String?,
    ): Either<SettingsPreferencesError, Unit> =
        updatePreference {
            it.copy(defaultCountryCode = countryCode, defaultCountryName = countryName)
        }

    companion object {
        private const val SETTINGS_PREFERENCES_FILE_NAME = "settings_preferences"
    }

    private fun SettingsPreferences.ThemeOptions.toAppNightMode(): AppNightMode =
        when (this) {
            SettingsPreferences.ThemeOptions.LIGHT -> AppNightMode.LIGHT
            SettingsPreferences.ThemeOptions.DARK -> AppNightMode.DARK
            SettingsPreferences.ThemeOptions.SYSTEM -> AppNightMode.SYSTEM
        }
}
