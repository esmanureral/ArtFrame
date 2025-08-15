package com.esmanureral.artframe

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ArtWorkDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val api: ApiService by lazy {
        ApiClient.getApi(getApplication())
    }

    private val _artworkDetail = MutableLiveData<ArtworkDetail?>()
    val artworkDetail: LiveData<ArtworkDetail?> get() = _artworkDetail

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
}
