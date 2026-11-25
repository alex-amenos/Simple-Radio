package com.alxnophis.simpleradio.api.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class CountryCacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val isCountryRequest = request.url.encodedPath.trimEnd('/') == "/json/countries"
        return if (isCountryRequest && response.isSuccessful) {
            response
                .newBuilder()
                .header("Cache-Control", CACHE_CONTROL_HEADER_VALUE)
                .removeHeader("Pragma")
                .build()
        } else {
            response
        }
    }

    companion object {
        const val CACHE_DURATION_SECONDS = 30 * 24 * 60 * 60 // 30 days = 2,592,000s
        const val CACHE_CONTROL_HEADER_VALUE = "public, max-age=$CACHE_DURATION_SECONDS"
    }
}
