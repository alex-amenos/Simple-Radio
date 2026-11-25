package com.alxnophis.jetpack.home.ui.viewmodel

import app.cash.turbine.test
import com.alxnophis.jetpack.home.ui.contract.HomeEvent
import com.alxnophis.jetpack.home.ui.contract.HomeTab
import com.alxnophis.jetpack.home.ui.contract.HomeUiState
import com.alxnophis.jetpack.testing.base.BaseViewModelUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class HomeViewModelUnitTests : BaseViewModelUnitTest() {
    @Test
    fun `GIVEN default setup WHEN view model is initialized THEN favorites tab is selected`() =
        runTest {
            val viewModel = HomeViewModel()

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo HomeUiState(selectedTab = HomeTab.Favorites)
                expectNoEvents()
            }
        }

    @Test
    fun `GIVEN favorites tab WHEN another tab is selected THEN state reflects the selected tab`() =
        runTest {
            val viewModel = HomeViewModel()

            viewModel.uiState.test {
                awaitItem().selectedTab shouldBeEqualTo HomeTab.Favorites
                viewModel.handleEvent(HomeEvent.SelectTab(HomeTab.Search))
                awaitItem().selectedTab shouldBeEqualTo HomeTab.Search
                viewModel.handleEvent(HomeEvent.SelectTab(HomeTab.Settings))
                awaitItem().selectedTab shouldBeEqualTo HomeTab.Settings
            }
        }
}
