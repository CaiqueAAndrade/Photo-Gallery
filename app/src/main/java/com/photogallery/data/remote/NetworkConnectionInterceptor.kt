package com.photogallery.data.remote

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

abstract class NetworkConnectionInterceptor : Interceptor {
    abstract fun isInternetAvailable(): Boolean

    abstract fun onInternetUnavailable()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!isInternetAvailable()) {
            onInternetUnavailable()

            val contentType = "text/html; charset=utf-8".toMediaTypeOrNull()
            return Response.Builder()
                .code(600)
                .request(chain.request())
                .protocol(Protocol.HTTP_2)
                .message("")
                .body("".toResponseBody(contentType))
                .build()
        }
        return chain.proceed(request)
    }
}

interface InternetConnectionListener {
    fun onInternetUnavailable()
}