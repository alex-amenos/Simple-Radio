package com.alxnophis.simpleradio.countryselector.ui.viewmodel

import arrow.core.left
import arrow.core.right
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import com.alxnophis.simpleradio.countryselector.data.model.Country
import com.alxnophis.simpleradio.countryselector.data.repository.CountrySelectorRepository
import com.alxnophis.simpleradio.countryselector.ui.contract.CountrySelectorEvent
import com.alxnophis.simpleradio.countryselector.ui.contract.CountrySelectorStatus
import com.alxnophis.simpleradio.testing.base.BaseViewModelUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
internal class CountrySelectorViewModelTest : BaseViewModelUnitTest() {
    private val countries =
        listOf(
            Country(code = "AD", name = "Andorra"),
            Country(code = "ES", name = "Spain"),
            Country(code = "FR", name = "France"),
        )

    @Test
    fun `GIVEN repository returns countries WHEN initialized THEN status is content with all countries`() =
        runTest {
            val repository =
                mock<CountrySelectorRepository> {
                    onBlocking { getCountries() } doReturn countries.right()
                }

            val viewModel = CountrySelectorViewModel(repository, initialCountryCode = "ES")
            testScheduler.advanceUntilIdle()

            viewModel.uiState.value.status shouldBeEqualTo CountrySelectorStatus.Content
            viewModel.uiState.value.countries shouldBeEqualTo countries
            viewModel.uiState.value.selectedCountryCode shouldBeEqualTo "ES"
        }

    @Test
    fun `GIVEN loaded countries WHEN query changed THEN list is filtered in memory`() =
        runTest {
            val repository =
                mock<CountrySelectorRepository> {
                    onBlocking { getCountries() } doReturn countries.right()
                }

            val viewModel = CountrySelectorViewModel(repository)
            testScheduler.advanceUntilIdle()

            viewModel.handleEvent(CountrySelectorEvent.QueryChanged("sp"))

            viewModel.uiState.value.countries shouldBeEqualTo listOf(Country(code = "ES", name = "Spain"))
            viewModel.uiState.value.status shouldBeEqualTo CountrySelectorStatus.Content
        }

    @Test
    fun `GIVEN query with no matches WHEN query changed THEN status is empty`() =
        runTest {
            val repository =
                mock<CountrySelectorRepository> {
                    onBlocking { getCountries() } doReturn countries.right()
                }

            val viewModel = CountrySelectorViewModel(repository)
            testScheduler.advanceUntilIdle()

            viewModel.handleEvent(CountrySelectorEvent.QueryChanged("nonexistent"))

            viewModel.uiState.value.countries shouldBeEqualTo emptyList()
            viewModel.uiState.value.status shouldBeEqualTo CountrySelectorStatus.Empty
        }

    @Test
    fun `GIVEN repository error WHEN initialized THEN status is error`() =
        runTest {
            val repository =
                mock<CountrySelectorRepository> {
                    onBlocking { getCountries() } doReturn UnexpectedCallError(RuntimeException()).left()
                }

            val viewModel = CountrySelectorViewModel(repository)
            testScheduler.advanceUntilIdle()

            viewModel.uiState.value.status shouldBeEqualTo CountrySelectorStatus.Error
        }
}
