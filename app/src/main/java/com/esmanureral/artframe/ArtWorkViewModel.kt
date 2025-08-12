package com.esmanureral.artframe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class ArtWorkViewModel : ViewModel() {
    private val _artworks = MutableLiveData<List<Artwork>>()
    val artworks: LiveData<List<Artwork>> get() = _artworks

    private val _artworkDetail = MutableLiveData<ArtworkDetail?>()
    val artworkDetail: LiveData<ArtworkDetail?> = _artworkDetail

    private val allArtworks = mutableListOf<Artwork>()
    private var currentPage = 1
    private var isLoading = false

    fun fetchArtworks() {
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            val response = ApiClient.api.getArtWorks(page = currentPage)
            if (response.isSuccessful) {
                val newData = response.body()?.data ?: emptyList()
                allArtworks.addAll(newData)
                _artworks.postValue(newData)
                currentPage++
            }
            isLoading = false
        }
    }

    fun fetchArtworkDetail(id: Int) {
        viewModelScope.launch {
            val response = ApiClient.api.getArtworkDetail(id)
            if (response.isSuccessful) {
                _artworkDetail.value = response.body()?.data
            } else {
                _artworkDetail.value = null
            }
        }
    }
}
