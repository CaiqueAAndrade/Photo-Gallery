package com.photogallery.model

import com.squareup.moshi.Json

data class UnsplashResponse(
    val description: String?,
    @field:Json(name = "alt_description")
    val altDescription: String,
    val color: String,
    val width: Int,
    val height: Int,
    @field:Json(name = "urls")
    val urls: Urls,
    @field:Json(name = "user")
    val user: User
)

data class Urls(
    val raw: String
)

data class User(
    @field:Json(name = "username")
    val userName: String
)