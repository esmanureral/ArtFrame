package com.esmanureral.artframe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ArtWorkViewModel(application: Application) : AndroidViewModel(application) {

    private val api: ApiService by lazy {
        ApiClient.getApi(getApplication())
    }

    private val _artworks = MutableLiveData<List<Artwork>>()
    val artworks: LiveData<List<Artwork>> get() = _artworks

    private val _artworkDetail = MutableLiveData<ArtworkDetail?>()
    val artworkDetail: LiveData<ArtworkDetail?> = _artworkDetail

    private val _artist = MutableLiveData<List<Artists>>()
    val artists: LiveData<List<Artists>> get() = _artist

    private val allArtworks = mutableListOf<Artwork>()
    private val allArtists = mutableListOf<Artists>()
    private var artistPage = 1
    private var currentPage = 1
    private var isLoading = false
    private var isLoadingArtists = false

    fun fetchArtworks() {
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            val response = api.getArtWorks(page = currentPage)
            if (response.isSuccessful) {
                val newData =
                    response.body()?.data?.filter { !it.imageId.isNullOrBlank() }
                        ?: emptyList()
                allArtworks.addAll(newData)
                _artworks.postValue(newData)
                currentPage++
            }
            isLoading = false
        }
    }

    fun fetchArtworkDetail(id: Int) {
        viewModelScope.launch {
            val response = api.getArtworkDetail(id)
            if (response.isSuccessful) {
                _artworkDetail.value = response.body()?.data
            } else {
                _artworkDetail.value = null
            }
        }
    }

    fun fetchArtists() {
        if (isLoadingArtists) return
        isLoadingArtists = true
        viewModelScope.launch {
            val response = api.getArtists(page = artistPage)
            if (response.isSuccessful) {
                val newData = response.body()?.data ?: emptyList()
                val artistsWithArtwork = newData.filter { artist ->
                    val artworksResponse = api.getArtworksByArtist(artist.id)
                    artworksResponse.isSuccessful &&
                            artworksResponse.body()?.data?.any { !it.imageId.isNullOrBlank() } == true
                }
                allArtists.addAll(artistsWithArtwork)
                _artist.postValue(allArtists)
                artistPage++
            }
            isLoadingArtists = false
        }
    }

    fun fetchArtworksByArtist(artistId: Int) {
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            val response = api.getArtworksByArtist(artistId)
            if (response.isSuccessful) {
                val artworksWithImages = response.body()?.data
                    ?.filter { !it.imageId.isNullOrBlank() }
                    ?: emptyList()

                _artworks.postValue(artworksWithImages)
            }
            isLoading = false
        }
    }
}