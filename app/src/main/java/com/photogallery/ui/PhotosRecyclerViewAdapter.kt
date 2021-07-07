package com.photogallery.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.photogallery.R
import com.photogallery.databinding.ItemPhotoBinding
import com.photogallery.model.UnsplashResponse

class PhotosRecyclerViewAdapter(private val favoriteClickListener: OnFavoriteClickListener) :
    RecyclerView.Adapter<PhotosRecyclerViewAdapter.PhotosViewHolder>() {

    interface OnFavoriteClickListener {
        fun favoriteClickListener(photoId: String, favoriteStatus: Boolean)
    }

    private var items: ArrayList<UnsplashResponse> = arrayListOf()

    fun setData(unsplashResponseList: List<UnsplashResponse>) {
        this.items.addAll(unsplashResponseList)
        notifyDataSetChanged()
    }

    fun clearData() {
        this.items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotosRecyclerViewAdapter.PhotosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemPhotoBinding>(
            inflater,
            R.layout.item_photo,
            parent,
            false
        )
        parent.isMotionEventSplittingEnabled = false
        return PhotosViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PhotosRecyclerViewAdapter.PhotosViewHolder,
        position: Int
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class PhotosViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(unsplashResponse: UnsplashResponse) {
            binding.apply {
                cpPhotoItem.setupView(unsplashResponse)
                cpPhotoItem.getFavoriteButton().setOnClickListener {
                    unsplashResponse.favorite = unsplashResponse.favorite != true
                    favoriteClickListener.favoriteClickListener(
                        unsplashResponse.id,
                        unsplashResponse.favorite ?: false
                    )
                    cpPhotoItem.updateButtonFavorite(unsplashResponse.favorite ?: false)
                }
            }
        }

    }
}