package com.esmanureral.artframe.presentation.artistlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.esmanureral.artframe.data.network.ApiClient
import com.esmanureral.artframe.data.network.ApiService
import com.esmanureral.artframe.data.network.Artists
import kotlinx.coroutines.launch

class ArtistListViewModel(application: Application) : AndroidViewModel(application) {

    private val api: ApiService by lazy {
        ApiClient.getApi(getApplication())
    }

    private val _artist = MutableLiveData<List<Artists>>()
    val artists: LiveData<List<Artists>> get() = _artist

    private val allArtists = mutableListOf<Artists>()
    private var artistPage = 1

    private var isLoadingArtists = false

    fun fetchArtists() {
        if (isLoadingArtists) return
        isLoadingArtists = true
        viewModelScope.launch {
            val response = api.getArtists(page = artistPage)
            if (response.isSuccessful) {
                val newData = response.body()?.data ?: emptyList()
                allArtists.addAll(newData)
                _artist.postValue(newData)
                artistPage++
            }
            isLoadingArtists = false
        }
    }
}