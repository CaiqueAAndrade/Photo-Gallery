package com.photogallery.di

import com.photogallery.data.remote.provideOkHttpClient
import com.photogallery.data.remote.provideServiceUnsplash
import com.photogallery.repository.PhotoGalleryRepository
import com.photogallery.ui.viewmodel.PhotoGalleryViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module {
    single { PhotoGalleryRepository(get()) }
}

val viewModelModule = module {
    viewModel { PhotoGalleryViewModel(androidApplication(), get()) }
}

val networkModule = module {
    single { provideOkHttpClient(androidContext()) }
    single { provideServiceUnsplash(get()) }
}