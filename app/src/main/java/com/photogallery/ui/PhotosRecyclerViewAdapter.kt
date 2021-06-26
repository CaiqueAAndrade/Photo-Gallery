package com.photogallery.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.photogallery.R
import com.photogallery.databinding.ItemPhotoBinding
import com.photogallery.model.UnsplashResponse

class PhotosRecyclerViewAdapter(
    val listener: PhotosItemClickListener
)  : RecyclerView.Adapter<PhotosRecyclerViewAdapter.PhotosViewHolder>(){

    private var items: ArrayList<UnsplashResponse> = arrayListOf()

    interface PhotosItemClickListener {
        fun photoItemClickListener(image: String)
    }

    fun setData(unsplashResponseList: List<UnsplashResponse>) {
        this.items.addAll(unsplashResponseList)
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
            RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(unsplashResponse: UnsplashResponse) {
            binding.cpPhotoItem.setupView(unsplashResponse)
        }

        override fun onClick(p0: View?) {
            listener.photoItemClickListener(items[adapterPosition].urls.raw)
        }

    }
}