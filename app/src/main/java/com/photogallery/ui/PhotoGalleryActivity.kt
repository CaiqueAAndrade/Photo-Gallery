package com.photogallery.ui

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doOnTextChanged
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
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*


class PhotoGalleryActivity : AppCompatActivity(), InternetConnectionListener {

    private lateinit var binding: ActivityPhotoGalleryBinding
    private lateinit var photoGalleryApplication: PhotoGalleryApplication
    private lateinit var internetConnectionListener: InternetConnectionListener
    private val adapter: PhotosRecyclerViewAdapter by inject()
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
            vm = viewModel
            lifecycleOwner = this@PhotoGalleryActivity

            setupStatusBarColor()
            setupEditTextBehavior()

            rvPhotos.adapter = adapter
            val linearLayoutManager =
                LinearLayoutManager(this@PhotoGalleryActivity, LinearLayoutManager.VERTICAL, false)
            val staggeredLayoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setupLinearRecyclerViewBehavior(linearLayoutManager)

            binding.smSelectListType.setOnCheckedChangeListener { _, b ->
                viewModel.onLayoutSelected(b)
                if (b) {
                    setupGridRecyclerViewBehavior(staggeredLayoutManager)
                } else {
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

        viewModel.shouldCleanRecyclerViewLiveData.observe(this, EventObserver {
            adapter.clearData()
        })
    }

    private fun setupGridRecyclerViewBehavior(layoutManager: StaggeredGridLayoutManager) {
        binding.apply {
            rvPhotos.layoutManager = layoutManager

            rvPhotos.addOnScrollListener(object : GridPaginationScrollListener(layoutManager) {
                override fun loadMoreItems() {
                    if (isListLoading) {
                        isListLoading = false
                        viewModel.getPhotos(etInputImageName.text.toString())
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
                        viewModel.getPhotos(etInputImageName.text.toString())
                    }
                }

            })
        }
    }


    private fun setupEditTextBehavior() {
        var timer: CountDownTimer? = null
        binding.etInputImageName.doOnTextChanged { text, _, _, _ ->
            viewModel.onLoading()
            timer?.cancel()
            timer = object : CountDownTimer(2500, 1500) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    viewModel.getPhotos(text.toString())
                }
            }
            timer?.start()
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
}