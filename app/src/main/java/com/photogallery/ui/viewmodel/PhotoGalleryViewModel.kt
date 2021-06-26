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

    private var currentPage = 0

    private val _photosListMutableLiveData = MutableLiveData<Event<List<UnsplashResponse>>>()
    val photosListLiveData: LiveData<Event<List<UnsplashResponse>>>
        get() = _photosListMutableLiveData

    private val _errorMutableLiveData = MutableLiveData<Event<String>>()
    val errorLiveData: LiveData<Event<String>>
        get() = _errorMutableLiveData

    fun getPhotos() {
        viewModelScope.launch {
            currentPage++
            when (val response = repository.getPhotos(currentPage)) {
                is Result.Success -> {
                    response.data?.let {
                        _photosListMutableLiveData.value = Event(it)
                    }
                }
                is Result.Failure -> {
                    val errorMessage =
                        getApplication<Application>().resources.getString(R.string.error_message)
                    _errorMutableLiveData.value =
                        Event(errorMessage.replace("%a", response.statusCode.toString()))
                }
                is Result.NetworkError -> {
                    val errorMessage =
                        getApplication<Application>().resources.getString(R.string.error_message)
                    val internetError =
                        getApplication<Application>().resources.getString(R.string.internet_error_description)
                    _errorMutableLiveData.value = Event(errorMessage.replace("%a", internetError))
                }
            }
        }
    }
}