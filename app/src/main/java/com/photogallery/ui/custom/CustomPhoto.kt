package com.photogallery.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
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
        Glide.with(context)
            .load(unsplashResponse.urls.raw)
            .centerCrop()
            .error(R.drawable.ic_launcher_background)
            .placeholder(R.drawable.ic_launcher_background)
            .into(photo)
        name.text = unsplashResponse.user.userName
        photo.contentDescription = unsplashResponse.altDescription
        description.text = if (!unsplashResponse.description.isNullOrEmpty()) {
            unsplashResponse.description
        } else {
            unsplashResponse.altDescription
        }
    }
}