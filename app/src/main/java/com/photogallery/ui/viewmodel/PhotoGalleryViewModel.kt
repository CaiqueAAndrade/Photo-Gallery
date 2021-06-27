package com.photogallery.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.photogallery.R
import com.photogallery.data.remote.Event
import com.photogallery.data.remote.Result
import com.photogallery.model.UnsplashResponse
import com.photogallery.repository.PhotoGalleryRepository
import kotlinx.coroutines.launch


class PhotoGalleryViewModel(
    mApplication: Application,
    private val repository: PhotoGalleryRepository
) : AndroidViewModel(mApplication) {

    private var recentImagesPageCounter = 0
    private var searchImagesPageCounter = 0

    private val _photosListMutableLiveData = MutableLiveData<Event<List<UnsplashResponse>>>()
    val photosListLiveData: LiveData<Event<List<UnsplashResponse>>>
        get() = _photosListMutableLiveData

    private val _shouldCleanRecyclerViewMutableLiveData = MutableLiveData<Event<Unit>>()
    val shouldCleanRecyclerViewLiveData: LiveData<Event<Unit>>
        get() = _shouldCleanRecyclerViewMutableLiveData

    private val _errorMutableLiveData = MutableLiveData<Event<String>>()
    val errorLiveData: LiveData<Event<String>>
        get() = _errorMutableLiveData

    private val _isLoadingMutableLiveData = MutableLiveData<Boolean>()
    val isLoadingLiveData: LiveData<Boolean>
        get() = _isLoadingMutableLiveData

    private val _isGridLayoutMutableLiveData = MutableLiveData<Boolean>()
    val isGridLayoutLiveData: LiveData<Boolean>
        get() = _isGridLayoutMutableLiveData

    /** Fetch photos by latest */
    private fun getLatestPhotos() {
        viewModelScope.launch {
            if (recentImagesPageCounter == 0) {
                _isLoadingMutableLiveData.value = true
            }
            searchImagesPageCounter = 0
            recentImagesPageCounter++
            when (val response = repository.getPhotos(recentImagesPageCounter)) {
                is Result.Success -> {
                    response.data?.let {
                        if (recentImagesPageCounter <= 1) {
                            _shouldCleanRecyclerViewMutableLiveData.value = Event(Unit)
                        }
                        _photosListMutableLiveData.value = Event(it)
                    }
                    _isLoadingMutableLiveData.value = false
                }
                is Result.Failure -> {
                    setupError(response.statusCode.toString())
                }
                is Result.NetworkError -> {
                    setupError()
                }
            }
        }
    }

    /** Fetch photos by name */
    fun getPhotos(query: String = "") {
        viewModelScope.launch {
            if (query.isEmpty()) {
                getLatestPhotos()
                return@launch
            }

            if (searchImagesPageCounter == 0) {
                _isLoadingMutableLiveData.value = true
            }

            recentImagesPageCounter = 0
            searchImagesPageCounter++
            when (val response = repository.searchPhotos(searchImagesPageCounter, query)) {
                is Result.Success -> {
                    response.data?.let {
                        if (searchImagesPageCounter <= 1) {
                            _shouldCleanRecyclerViewMutableLiveData.value = Event(Unit)
                        }
                        _photosListMutableLiveData.value = Event(it)
                    }
                    _isLoadingMutableLiveData.value = false
                }
                is Result.Failure -> {
                    setupError(response.statusCode.toString())
                }
                is Result.NetworkError -> {
                    setupError()
                }
            }
        }
    }

    /** Update view with loading ProgressBar */
    fun onLoading() {
        _isLoadingMutableLiveData.value = true
    }

    /** Update textView layout style on Switcher */
    fun onLayoutSelected(isGridLayout: Boolean) {
        _isGridLayoutMutableLiveData.value = isGridLayout
    }


    /** Method used to setup error message on the screen */
    private fun setupError(errorCode: String? = null) {
        _isLoadingMutableLiveData.value = false
        if (errorCode != null) {
            val errorMessage =
                getApplication<Application>().resources.getString(R.string.error_message)
            _errorMutableLiveData.value =
                Event(errorMessage.replace("%a", errorCode))
        } else {
            val errorMessage =
                getApplication<Application>().resources.getString(R.string.error_message)
            val internetError =
                getApplication<Application>().resources.getString(R.string.internet_error_description)
            _errorMutableLiveData.value = Event(errorMessage.replace("%a", internetError))
        }
    }
}