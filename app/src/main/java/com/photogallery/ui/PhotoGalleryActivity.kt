package com.photogallery.ui

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.photogallery.PhotoGalleryApplication
import com.photogallery.R
import com.photogallery.data.remote.EventObserver
import com.photogallery.data.remote.InternetConnectionListener
import com.photogallery.databinding.ActivityPhotoGalleryBinding
import com.photogallery.ui.viewmodel.PhotoGalleryViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class PhotoGalleryActivity : AppCompatActivity(), InternetConnectionListener,
    PhotosRecyclerViewAdapter.PhotosItemClickListener {

    private lateinit var binding: ActivityPhotoGalleryBinding
    private lateinit var photoGalleryApplication: PhotoGalleryApplication
    private lateinit var internetConnectionListener: InternetConnectionListener
    private val adapter = PhotosRecyclerViewAdapter(this)

    private val viewModel by viewModel<PhotoGalleryViewModel> {
        parametersOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_photo_gallery)

        photoGalleryApplication = application as PhotoGalleryApplication

        binding.rvPhotos.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        binding.rvPhotos.adapter = adapter

        setupStatusBarColor()
        setInternetConnectionListener(this)
        viewModel.getPhotos()
        subscribe()
    }

    override fun onResume() {
        super.onResume()
        photoGalleryApplication.setInternetConnectionListener(this)
    }

    private fun subscribe() {
        viewModel.photosListLiveData.observe(this, EventObserver {
            adapter.setData(it)
        })
    }

    private fun setupStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val wic = WindowInsetsControllerCompat(window, window.decorView)
            wic.isAppearanceLightStatusBars = true
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        }
    }

    override fun onInternetUnavailable() {
        if (this::internetConnectionListener.isInitialized) {
            internetConnectionListener.onInternetUnavailable()
        }
    }

    private fun setInternetConnectionListener(listener: InternetConnectionListener) {
        this.internetConnectionListener = listener
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun photoItemClickListener(image: String) {
        Toast.makeText(this, image, Toast.LENGTH_LONG).show()
    }
}