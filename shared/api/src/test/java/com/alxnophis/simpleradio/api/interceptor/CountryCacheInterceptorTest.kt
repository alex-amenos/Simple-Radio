package com.alxnophis.simpleradio.api.interceptor

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

internal class CountryCacheInterceptorTest {
    private val interceptor = CountryCacheInterceptor()

    @Test
    fun `GIVEN request for json countries WHEN response successful THEN Cache-Control header has 30 day max-age`() {
        val request = Request.Builder().url("https://de1.api.radio-browser.info/json/countries").build()
        val originalResponse =
            Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("[]".toResponseBody("application/json".toMediaType()))
                .build()

        val chain =
            mock<Interceptor.Chain> {
                on { request() } doAnswer { request }
                on { proceed(any()) } doAnswer { originalResponse }
            }

        val result = interceptor.intercept(chain)

        result.header("Cache-Control") shouldBeEqualTo "public, max-age=2592000"
    }

    @Test
    fun `GIVEN request for stations search WHEN response successful THEN Cache-Control header is untouched`() {
        val request = Request.Builder().url("https://de1.api.radio-browser.info/json/stations/search?name=jazz").build()
        val originalResponse =
            Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("[]".toResponseBody("application/json".toMediaType()))
                .build()

        val chain =
            mock<Interceptor.Chain> {
                on { request() } doAnswer { request }
                on { proceed(any()) } doAnswer { originalResponse }
            }

        val result = interceptor.intercept(chain)

        result.header("Cache-Control").shouldBeNull()
    }
}
