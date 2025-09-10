package com.esmanureral.artframe.presentation.artistdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.artframe.data.network.ApiService
import com.esmanureral.artframe.data.network.Artwork
import kotlinx.coroutines.launch

class ArtistDetailViewModel(private val apiService: ApiService) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _artworks = MutableLiveData<List<Artwork>>()
    val artworks: LiveData<List<Artwork>> get() = _artworks

    fun fetchArtworksByArtist(artistId: Int) {
        if (_isLoading.value == true) return
        _isLoading.value = true

        viewModelScope.launch {
            val response = apiService.getArtworksByArtist(artistId)
            if (response.isSuccessful) {
                val artworksWithImages = response.body()?.data
                    ?.filter { !it.imageId.isNullOrBlank() }
                    ?: emptyList()

                _artworks.postValue(artworksWithImages)
            }
            _isLoading.postValue(false)
        }
    }
}