package com.esmanureral.artframe.presentation.artistdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.esmanureral.artframe.data.network.ApiClient
import com.esmanureral.artframe.data.network.ApiService
import com.esmanureral.artframe.data.network.Artwork
import kotlinx.coroutines.launch

class ArtistDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val api: ApiService by lazy {
        ApiClient.getApi(getApplication())
    }

    private var isLoading = false

    private val _artworks = MutableLiveData<List<Artwork>>()
    val artworks: LiveData<List<Artwork>> get() = _artworks

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