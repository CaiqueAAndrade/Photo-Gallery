package com.photogallery

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import com.photogallery.data.remote.Event
import com.photogallery.data.remote.provideOkHttpClient
import com.photogallery.data.remote.provideServiceUnsplash
import com.photogallery.model.UnsplashResponse
import com.photogallery.model.Urls
import com.photogallery.model.User
import com.photogallery.repository.PhotoGalleryRepository
import com.photogallery.ui.viewmodel.PhotoGalleryViewModel
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
@LargeTest
class PhotoGalleryViewModelTest {

    @Mock
    private lateinit var viewModel: PhotoGalleryViewModel

    @Mock
    private lateinit var isLoadingLiveData: LiveData<Boolean>

    @Mock
    private lateinit var isGridLayoutLiveData: LiveData<Boolean>

    @Mock
    private lateinit var unsplashResponseList: LiveData<Event<List<UnsplashResponse>>>

    @Mock
    private lateinit var errorLiveData: LiveData<Event<String>>

    @Mock
    private lateinit var booleanObserver: Observer<in Boolean>

    @Mock
    private lateinit var dataObserver: Observer<in Event<List<UnsplashResponse>>>

    @Mock
    private lateinit var errorObserver: Observer<in Event<String>>

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val okHttpClient = provideOkHttpClient(ApplicationProvider.getApplicationContext())
        val providerServiceUnsplashApi = provideServiceUnsplash(okHttpClient)
        viewModel = spy(
            PhotoGalleryViewModel(
                ApplicationProvider.getApplicationContext(),
                PhotoGalleryRepository(providerServiceUnsplashApi)
            )
        )
        isLoadingLiveData = viewModel.isLoadingLiveData
        isGridLayoutLiveData = viewModel.isGridLayoutLiveData
        unsplashResponseList = viewModel.photosListLiveData
        errorLiveData = viewModel.errorLiveData
    }

    @Test
    fun `Verify isLoadingLiveData values changes on getPhotos`() {
        //when
        assertNotNull(viewModel.getPhotos())
        viewModel.isLoadingLiveData.observeForever(booleanObserver)

        //then
        verify(booleanObserver).onChanged(true)

        //when
        viewModel.getPhotos()

        //then
        verify(booleanObserver).onChanged(true)
        isLoadingLiveData.value?.let { assertTrue(it) }
    }

    @Test
    fun `Verify isGridLayoutLiveData values changes on onLayoutSelected`() {
        //when
        viewModel.isGridLayoutLiveData.observeForever(booleanObserver)

        //given
        viewModel.onLayoutSelected(isGridLayout = true)

        //then
        verify(booleanObserver).onChanged(true)
        isGridLayoutLiveData.value?.let { assertTrue(it) }

        //given
        viewModel.onLayoutSelected(isGridLayout = false)

        //then
        verify(booleanObserver).onChanged(true)
        isGridLayoutLiveData.value?.let { assertFalse(it) }

    }

    @Test
    fun `Verify isLoadingLiveData values changes on onLoading`() {
        //when
        assertNotNull(viewModel.onLoading())
        viewModel.isLoadingLiveData.observeForever(booleanObserver)

        //then
        verify(booleanObserver).onChanged(true)
        isLoadingLiveData.value?.let { assertTrue(it) }
    }

    @Test
    fun `Verify getPhotos values is not null when called getPhotos`() {
        //given
        val photosMutableLiveData = MutableLiveData<Event<List<UnsplashResponse>>>()
        val unsplashResponse = Event(
            listOf(
                UnsplashResponse(
                    "",
                    "", "", 0,
                    0, Urls(""),
                    User("")
                )
            )
        )
        photosMutableLiveData.value = unsplashResponse
        unsplashResponseList = photosMutableLiveData

        //when
        `when`(viewModel.photosListLiveData).thenReturn(unsplashResponseList)
        viewModel.getPhotos()
        viewModel.photosListLiveData.observeForever(dataObserver)

        //then
        verify(dataObserver).onChanged(unsplashResponse)
        assertNotNull(viewModel.photosListLiveData)
    }

    @Test
    fun `Verify getPhotos error is not null when called getPhotos with error` () {
        //given
        val photosMutableLiveData = MutableLiveData<Event<String>>()
        val errorEvent = Event("Error")
        photosMutableLiveData.value = errorEvent
        errorLiveData = photosMutableLiveData

        //when
        `when`(viewModel.errorLiveData).thenReturn(errorLiveData)
        viewModel.getPhotos()
        viewModel.errorLiveData.observeForever(errorObserver)

        //then
        verify(errorObserver).onChanged(errorEvent)
        assertNotNull(viewModel.errorLiveData.value)
    }
}