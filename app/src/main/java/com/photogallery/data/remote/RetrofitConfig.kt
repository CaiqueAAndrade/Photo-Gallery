package com.photogallery.data.remote

import android.content.Context
import com.photogallery.BuildConfig
import com.photogallery.PhotoGalleryApplication
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

fun provideOkHttpClient(context: Context): OkHttpClient.Builder {
    val timeout = 30L

    val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    val cookieManager = CookieManager()
    cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

    val client = OkHttpClient.Builder()
    client.apply {
        connectTimeout(timeout, TimeUnit.SECONDS)
        readTimeout(timeout, TimeUnit.SECONDS)
        writeTimeout(timeout, TimeUnit.SECONDS)

        client.cookieJar(JavaNetCookieJar(cookieManager))

        addInterceptor(interceptor)
        addInterceptor(object : NetworkConnectionInterceptor() {
            override fun isInternetAvailable(): Boolean {
                return (context as PhotoGalleryApplication).isInternetAvailable()
            }

            override fun onInternetUnavailable() {
                (context as PhotoGalleryApplication).onInternetUnavailable()
            }

        })
    }

    return client
}

fun provideServiceUnsplash(client: OkHttpClient.Builder): UnsplashApi {
//    Unsplash api mock in case is used more than 50 requests in 1 hour
//    .baseUrl("https://private-00cd68-caique1.apiary-mock.com/")
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.unsplash.com/")
        .addCallAdapterFactory(CallAdapterFactory())
        .addConverterFactory(MoshiConverterFactory.create())
        .client(client.build())
        .build()

    return retrofit.create(UnsplashApi::class.java)
}