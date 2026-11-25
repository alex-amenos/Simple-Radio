package com.alxnophis.jetpack.home.domain.usecase

import com.alxnophis.jetpack.home.domain.model.Feature
import com.alxnophis.jetpack.home.domain.model.NavigationItem

class GetNavigationItemsUseCase {
    operator fun invoke(): List<NavigationItem> = defaultNavigationItems

    companion object {
        private val settings =
            NavigationItem(
                name = "Settings",
                emoji = "⚙️",
                description = "Settings options screen",
                feature = Feature.Settings,
            )
        private val radioStreaming =
            NavigationItem(
                name = "Radio Streaming",
                emoji = "📻",
                description = "Listen to live Spanish radio stations",
                feature = Feature.RadioStreaming,
            )

        val defaultNavigationItems =
            listOf(
                radioStreaming,
                settings,
            )
    }
}
