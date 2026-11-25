package com.alxnophis.jetpack.root.ui.navigation

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.alxnophis.jetpack.home.domain.model.Feature
import com.alxnophis.jetpack.home.ui.composable.HomeFeature
import com.alxnophis.jetpack.radio.ui.composable.RadioPlayerFeature
import com.alxnophis.jetpack.radio.ui.composable.RadioStationListFeature
import com.alxnophis.jetpack.settings.ui.navigation.SettingsFeature

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@SuppressLint("ComposeModifierMissing")
@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    openRadioPlayer: Boolean = false,
    onRadioPlayerOpened: () -> Unit = {},
) {
    val context = LocalContext.current
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

    fun showRadioPlayer() {
        if (backStack.last() is Route.RadioPlayer) return
        if (backStack.last() !is Route.RadioStations) backStack.add(Route.RadioStations)
        backStack.add(Route.RadioPlayer)
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
                        onNavigateTo = { feature: Feature ->
                            val route: NavKey =
                                when (feature) {
                                    Feature.RadioStreaming -> Route.RadioStations
                                    Feature.Settings -> Route.Settings
                                }
                            backStack.add(route)
                        },
                        onBack = onBack,
                    )
                }
                entry<Route.Settings> {
                    SettingsFeature(
                        onBack = onBack,
                    )
                }
                entry<Route.RadioStations> {
                    RadioStationListFeature(
                        onBack = onBack,
                        onStationSelected = { backStack.add(Route.RadioPlayer) },
                    )
                }
                entry<Route.RadioPlayer> {
                    RadioPlayerFeature(
                        onBack = onBack,
                    )
                }
            },
    )
}
