package com.esmanureral.artframe.presentation.artistlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.esmanureral.artframe.data.network.ApiClient
import com.esmanureral.artframe.data.network.ApiService
import com.esmanureral.artframe.presentation.artistlist.model.ArtistListUI
import com.esmanureral.artframe.presentation.artistlist.model.toUIModel
import kotlinx.coroutines.launch

class ArtistListViewModel(application: Application) : AndroidViewModel(application) {

    private val api: ApiService by lazy {
        ApiClient.getApi(getApplication())
    }

    private val _artist = MutableLiveData<List<ArtistListUI>>()
    val artists: LiveData<List<ArtistListUI>> get() = _artist

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val allArtists = mutableListOf<ArtistListUI>()
    private var artistPage = 1
    private var isLoadingArtists = false

    fun fetchArtists() {
        if (isLoadingArtists) return
        isLoadingArtists = true
        _isLoading.value = true

        viewModelScope.launch {
            val response = api.getArtists(page = artistPage)
            if (response.isSuccessful) {
                val newData = response.body()?.data ?: emptyList()
                val uiData = newData.map { it.toUIModel() }
                allArtists.addAll(uiData)
                _artist.postValue(uiData)
                artistPage++
            }
            isLoadingArtists = false
            _isLoading.postValue(false)
        }
    }
}