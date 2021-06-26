package com.photogallery.data.remote

import com.photogallery.model.UnsplashResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {

    @GET("/photos")
    suspend fun getPhotos(
        @Query("page") page: Int,
        @Query("client_id") clientId: String
    ): Result<List<UnsplashResponse>>
}