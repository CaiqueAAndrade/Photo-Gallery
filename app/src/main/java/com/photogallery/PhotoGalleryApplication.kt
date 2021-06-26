package com.photogallery

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.photogallery.data.remote.InternetConnectionListener
import com.photogallery.di.networkModule
import com.photogallery.di.repositoryModule
import com.photogallery.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PhotoGalleryApplication : Application() {

    private lateinit var internetConnectionListener: InternetConnectionListener

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PhotoGalleryApplication)
            modules(
                listOf(
                    repositoryModule,
                    viewModelModule,
                    networkModule
                )
            )
        }
    }

    fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun onInternetUnavailable() {
        if (this::internetConnectionListener.isInitialized) {
            internetConnectionListener.onInternetUnavailable()
        }
    }

    fun setInternetConnectionListener(listener: InternetConnectionListener) {
        this.internetConnectionListener = listener
    }
}