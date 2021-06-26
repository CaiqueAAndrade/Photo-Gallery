package com.photogallery.repository

import com.photogallery.data.remote.Result
import com.photogallery.data.remote.UnsplashApi
import com.photogallery.model.UnsplashResponse
import com.photogallery.util.Constants

class PhotoGalleryRepository(private val service: UnsplashApi) {

    suspend fun getPhotos(page: Int): Result<List<UnsplashResponse>> {
        return service.getPhotos(
            page = page,
            clientId = Constants.UnsplashApi.UNSPLASH_API_KEY
        )
    }
}