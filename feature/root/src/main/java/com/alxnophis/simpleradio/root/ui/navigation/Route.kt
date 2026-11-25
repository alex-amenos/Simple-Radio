package com.alxnophis.simpleradio.root.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Route : NavKey {
    @Serializable
    data object Home : Route()

    @Serializable
    data object RadioPlayer : Route()

    @Serializable
    data class CountrySelector(
        val selectedCountryCode: String?,
        val caller: CountrySelectorCaller,
    ) : Route()

    @Serializable
    enum class CountrySelectorCaller {
        SEARCH,
        SETTINGS,
    }
}
