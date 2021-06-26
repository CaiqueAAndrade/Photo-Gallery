package com.photogallery.ui

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.photogallery.PhotoGalleryApplication
import com.photogallery.R
import com.photogallery.data.remote.EventObserver
import com.photogallery.data.remote.InternetConnectionListener
import com.photogallery.databinding.ActivityPhotoGalleryBinding
import com.photogallery.ui.viewmodel.PhotoGalleryViewModel
import com.photogallery.util.GridPaginationScrollListener
import com.photogallery.util.LinearPaginationScrollListener
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class PhotoGalleryActivity : AppCompatActivity(), InternetConnectionListener,
    PhotosRecyclerViewAdapter.PhotosItemClickListener {

    private lateinit var binding: ActivityPhotoGalleryBinding
    private lateinit var photoGalleryApplication: PhotoGalleryApplication
    private lateinit var internetConnectionListener: InternetConnectionListener
    private val adapter = PhotosRecyclerViewAdapter(this)
    private var isListLoading = true

    private val viewModel by viewModel<PhotoGalleryViewModel> {
        parametersOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_photo_gallery)

        photoGalleryApplication = application as PhotoGalleryApplication


        setupLayoutBehavior()
        setInternetConnectionListener(this)
        viewModel.getPhotos()
        subscribe()
    }

    override fun onResume() {
        super.onResume()
        photoGalleryApplication.setInternetConnectionListener(this)
    }

    private fun setupStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val wic = WindowInsetsControllerCompat(window, window.decorView)
            wic.isAppearanceLightStatusBars = true
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        }
    }

    private fun setupLayoutBehavior() {
        binding.apply {
            setupStatusBarColor()

            rvPhotos.adapter = adapter
            val linearLayoutManager =
                LinearLayoutManager(this@PhotoGalleryActivity, LinearLayoutManager.VERTICAL, false)
            val staggeredLayoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setupLinearRecyclerViewBehavior(linearLayoutManager)

            binding.smSelectListType.setOnCheckedChangeListener { _, b ->
                if (b) {
                    tvGridOption.setTypeface(null, Typeface.BOLD)
                    tvLinearOption.setTypeface(null, Typeface.NORMAL)
                    setupGridRecyclerViewBehavior(staggeredLayoutManager)
                } else {
                    tvLinearOption.setTypeface(null, Typeface.BOLD)
                    tvGridOption.setTypeface(null, Typeface.NORMAL)
                    setupLinearRecyclerViewBehavior(linearLayoutManager)
                }
            }
        }
    }

    private fun subscribe() {
        viewModel.photosListLiveData.observe(this, EventObserver {
            isListLoading = true
            adapter.setData(it)
        })

        viewModel.errorLiveData.observe(this, EventObserver {
            Snackbar.make(findViewById(android.R.id.content), it, Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun setupGridRecyclerViewBehavior(layoutManager: StaggeredGridLayoutManager) {
        binding.apply {
            rvPhotos.layoutManager = layoutManager

            rvPhotos.addOnScrollListener(object : GridPaginationScrollListener(layoutManager) {
                override fun loadMoreItems() {
                    if (isListLoading) {
                        isListLoading = false
                        viewModel.getPhotos()
                    }
                }

            })
        }
    }

    private fun setupLinearRecyclerViewBehavior(layoutManager: LinearLayoutManager) {
        binding.apply {
            rvPhotos.layoutManager = layoutManager

            rvPhotos.addOnScrollListener(object : LinearPaginationScrollListener(layoutManager) {
                override fun loadMoreItems() {
                    if (isListLoading) {
                        isListLoading = false
                        viewModel.getPhotos()
                    }
                }

            })
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