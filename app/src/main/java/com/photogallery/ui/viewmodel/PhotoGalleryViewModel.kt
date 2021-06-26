package com.photogallery.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.photogallery.data.remote.Event
import com.photogallery.data.remote.Result
import com.photogallery.model.UnsplashResponse
import com.photogallery.repository.PhotoGalleryRepository
import kotlinx.coroutines.launch

class PhotoGalleryViewModel(
    mApplication: Application,
    private val repository: PhotoGalleryRepository
) : AndroidViewModel(mApplication) {

    private val _photosListMutableLiveData = MutableLiveData<Event<List<UnsplashResponse>>>()
    val photosListLiveData: LiveData<Event<List<UnsplashResponse>>>
        get() = _photosListMutableLiveData

    fun getPhotos() {
        viewModelScope.launch {
            when (val response = repository.getPhotos(1)) {
                is Result.Success -> {
                    response.data?.let {
                        _photosListMutableLiveData.value = Event(it)
                    }
                }
                is Result.Failure -> {
                    //TODO: SOME ERROR MESSAGE
                }
                is Result.NetworkError -> {
                    //TODO: SOME ERROR MESSAGE
                }
            }
        }
    }
}