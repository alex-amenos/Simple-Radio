package com.alxnophis.simpleradio.home.ui.composable

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.alxnophis.simpleradio.home.ui.contract.HomeUiState
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
internal class HomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `GIVEN miniPlayerContent WHEN HomeScreen is displayed THEN miniPlayerContent is rendered`() {
        val testTag = "test_mini_player"
        composeRule.setContent {
            HomeScreen(
                uiState = HomeUiState(),
                handleEvent = {},
                favoritesContent = { Text(text = "Favorites") },
                searchContent = { Text(text = "Search") },
                settingsContent = { Text(text = "Settings") },
                miniPlayerContent = { Text(text = "Mini Player", modifier = Modifier.testTag(testTag)) },
                onBack = {},
            )
        }

        composeRule.onNodeWithTag(testTag).assertIsDisplayed()
        composeRule.onNodeWithTag(HomeTags.TAG_HOME_BOTTOM_BAR).assertIsDisplayed()
    }
}
