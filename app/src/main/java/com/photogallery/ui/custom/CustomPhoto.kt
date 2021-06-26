package com.photogallery.ui.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.photogallery.R
import com.photogallery.model.UnsplashResponse


class CustomPhoto @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var photo: ImageView
    private var name: TextView
    private var description: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_photo, this, true)
        photo = findViewById(R.id.iv_photo)
        name = findViewById(R.id.tv_name)
        description = findViewById(R.id.tv_description)
    }

    fun setupView(unsplashResponse: UnsplashResponse) {

        /** Creating a drawable placeholder with the same color that the API brings. */
        val unwrappedDrawable =
            AppCompatResources.getDrawable(context, R.drawable.ic_launcher_background)
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(unsplashResponse.color))

        /**
         *   Using Glide to load images from Network.
         *   With ".diskCacheStrategy(DiskCacheStrategy.DATA)"
         *   Glide already stores the image on cache and loads it from there in case needed.
         */
        Glide.with(context)
            .load(unsplashResponse.urls.raw)
            .centerCrop()
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .error(unwrappedDrawable)
            .placeholder(unwrappedDrawable)
            .into(photo)

        name.text = unsplashResponse.user.userName
        photo.contentDescription = unsplashResponse.altDescription

        /**
         * Because not every image comes with a description this code verifies
         * if the description is null so can replace with altDescription
         */
        description.text = if (!unsplashResponse.description.isNullOrEmpty()) {
            unsplashResponse.description
        } else {
            unsplashResponse.altDescription
        }
    }
}