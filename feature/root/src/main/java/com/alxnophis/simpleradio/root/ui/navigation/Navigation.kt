package com.alxnophis.simpleradio.root.ui.navigation

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.alxnophis.simpleradio.countryselector.data.model.Country
import com.alxnophis.simpleradio.countryselector.ui.composable.CountrySelectorFeature
import com.alxnophis.simpleradio.favorites.ui.composable.FavoritesFeature
import com.alxnophis.simpleradio.home.ui.composable.HomeFeature
import com.alxnophis.simpleradio.radioplayer.playback.RadioPlayback
import com.alxnophis.simpleradio.radioplayer.ui.composable.RadioPlayerBarFeature
import com.alxnophis.simpleradio.radioplayer.ui.composable.RadioPlayerFeature
import com.alxnophis.simpleradio.search.ui.composable.SearchFeature
import com.alxnophis.simpleradio.settings.ui.navigation.SettingsFeature
import com.alxnophis.simpleradio.storage.favorites.model.FavoriteStation
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@SuppressLint("ComposeModifierMissing")
@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    openRadioPlayer: Boolean = false,
    onRadioPlayerOpened: () -> Unit = {},
) {
    val context = LocalContext.current
    val radioPlayback = koinInject<RadioPlayback>()
    val backStack = rememberNavBackStack(Route.Home)
    val onBack: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        } else {
            (context as? Activity)?.finishAffinity()
        }
    }
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val customDirective = calculatePaneScaffoldDirective(adaptiveInfo).copy(horizontalPartitionSpacerSize = 0.dp)
    val listDetailStrategy = rememberListDetailSceneStrategy<Any>(directive = customDirective)

    var searchCountryResult by remember { mutableStateOf<Country?>(null) }
    var hasNewSearchCountry by remember { mutableStateOf(false) }

    var settingsCountryResult by remember { mutableStateOf<Pair<String?, String?>?>(null) }
    var hasNewSettingsCountry by remember { mutableStateOf(false) }

    fun showRadioPlayer() {
        if (backStack.last() is Route.RadioPlayer) return
        backStack.add(Route.RadioPlayer)
    }

    fun playStations(
        stations: List<FavoriteStation>,
        index: Int,
    ) {
        radioPlayback.play(stations, index)
        showRadioPlayer()
    }

    LaunchedEffect(openRadioPlayer) {
        if (!openRadioPlayer) return@LaunchedEffect
        showRadioPlayer()
        onRadioPlayerOpened()
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = onBack,
        sceneStrategies = listOf(listDetailStrategy),
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(targetOffsetX = { it })
        },
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
        entryProvider =
            entryProvider {
                entry<Route.Home> {
                    HomeFeature(
                        favoritesContent = { FavoritesFeature(onStationSelected = ::playStations) },
                        searchContent = {
                            SearchFeature(
                                onStationSelected = ::playStations,
                                onSelectCountry = { currentCode ->
                                    backStack.add(
                                        Route.CountrySelector(
                                            selectedCountryCode = currentCode,
                                            caller = Route.CountrySelectorCaller.SEARCH,
                                        ),
                                    )
                                },
                                selectedCountry = searchCountryResult,
                                hasNewCountrySelection = hasNewSearchCountry,
                            )
                        },
                        settingsContent = {
                            SettingsFeature(
                                onSelectCountry = { currentCode ->
                                    backStack.add(
                                        Route.CountrySelector(
                                            selectedCountryCode = currentCode,
                                            caller = Route.CountrySelectorCaller.SETTINGS,
                                        ),
                                    )
                                },
                                selectedCountry = settingsCountryResult,
                                hasNewCountrySelection = hasNewSettingsCountry,
                            )
                        },
                        onBack = onBack,
                        miniPlayerContent = { RadioPlayerBarFeature(onOpenPlayer = ::showRadioPlayer) },
                    )
                }
                entry<Route.RadioPlayer> {
                    RadioPlayerFeature(
                        onBack = onBack,
                    )
                }
                entry<Route.CountrySelector> { key ->
                    CountrySelectorFeature(
                        selectedCountryCode = key.selectedCountryCode,
                        onCountrySelected = { country ->
                            when (key.caller) {
                                Route.CountrySelectorCaller.SEARCH -> {
                                    searchCountryResult = country
                                    hasNewSearchCountry = true
                                }

                                Route.CountrySelectorCaller.SETTINGS -> {
                                    settingsCountryResult = country?.let { it.code to it.name } ?: (null to null)
                                    hasNewSettingsCountry = true
                                }
                            }
                            onBack()
                        },
                        onBack = onBack,
                    )
                }
            },
    )
}
