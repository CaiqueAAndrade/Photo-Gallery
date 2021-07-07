package com.photogallery.ui.custom

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.photogallery.R
import com.photogallery.model.UnsplashResponse


class CustomPhoto @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var photo: ImageView
    private var name: TextView
    private var description: TextView
    private var favoriteButton: Button

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_photo, this, true)
        photo = findViewById(R.id.iv_photo)
        name = findViewById(R.id.tv_name)
        description = findViewById(R.id.tv_description)
        favoriteButton = findViewById(R.id.bt_favorite)
    }

    fun setupView(unsplashResponse: UnsplashResponse) {
        name.text = unsplashResponse.user.userName
        photo.contentDescription = unsplashResponse.altDescription

        /*
         * Because not every image comes with a description this code verifies
         * if the description is null, so can replace it with altDescription
         */
        val correctDescription = if (!unsplashResponse.description.isNullOrEmpty()) {
            unsplashResponse.description
        } else {
            unsplashResponse.altDescription
        }
        description.text = correctDescription

        // Creating placeholder
        val placeholder =
            getBackgroundImagePlaceholder(
                color = unsplashResponse.color,
                width = unsplashResponse.width,
                height = unsplashResponse.height
            )

        // loading image with Glide
        loadGlideImageUrl(
            unsplashResponse = unsplashResponse,
            placeholder = placeholder,
            correctDescription = correctDescription
        )


        updateButtonFavorite(unsplashResponse.favorite ?: false)


    }

    /**
     *   Using Glide to load images from Network.
     *   With ".diskCacheStrategy(DiskCacheStrategy.DATA)"
     *   Glide already stores the image on cache and loads it from there in case needed.
     */
    private fun loadGlideImageUrl(
        unsplashResponse: UnsplashResponse,
        placeholder: Drawable,
        correctDescription: String
    ) {
        Glide.with(context)
            .load(unsplashResponse.urls.raw)
            .centerCrop()
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .placeholder(placeholder)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    photo.setImageDrawable(placeholder)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    setupZoomBehavior(
                        placeholder = placeholder,
                        userName = unsplashResponse.user.userName,
                        description = correctDescription,
                        image = resource
                    )
                    return false
                }
            })
            .into(photo)
    }

    /**
     * Set image click listener after image is loaded
     * Use already loaded image to create the dialog
     */
    private fun setupZoomBehavior(
        placeholder: Drawable,
        userName: String,
        description: String,
        image: Drawable?
    ) {
        photo.setOnClickListener {
            val builder = AlertDialog.Builder(context).create()
            val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_image_zoom, null)
            val imageView: ImageView = view.findViewById(R.id.iv_image_zoom)
            val closeButton: Button = view.findViewById(R.id.bt_close)

            builder.setTitle(userName)
            builder.setMessage(description)
            builder.setView(view)
            builder.setCancelable(true)
            builder.show()

            closeButton.setOnClickListener {
                builder.dismiss()
            }
            imageView.setImageDrawable(image ?: placeholder)
        }
    }

    /** Creating a drawable placeholder with the same color that the API brings.
     * Set correct image size
     * */
    private fun getBackgroundImagePlaceholder(color: String, width: Int, height: Int): Drawable {
        val unwrappedDrawable =
            AppCompatResources.getDrawable(context, R.drawable.bg_placeholder)
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(color))
        wrappedDrawable.setBounds(0, 0, width, height)
        return wrappedDrawable
    }

    fun getFavoriteButton(): Button = favoriteButton

    fun updateButtonFavorite(isFavorite: Boolean) {
        if (isFavorite) {
            favoriteButton.setCompoundDrawablesWithIntrinsicBounds(
                null,
                ContextCompat.getDrawable(context, R.drawable.ic_star_full),
                null,
                null
            )
        } else {
            favoriteButton.setCompoundDrawablesWithIntrinsicBounds(
                null,
                ContextCompat.getDrawable(context, R.drawable.ic_star_empty),
                null,
                null
            )
        }
    }
}