package com.esmanureral.artframe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class ArtWorkViewModel : ViewModel() {
    private val _artworks = MutableLiveData<List<Artwork>>()
    val artworks: LiveData<List<Artwork>> = _artworks

    fun fetchArtworks() {
        viewModelScope.launch {
            val response = ApiClient.api.getArtWorks()
            if (response.isSuccessful) {
                val artworks = response.body()?.data ?: emptyList()
                _artworks.value = artworks
            } else {
                _artworks.value = emptyList()
            }
        }
    }

}
