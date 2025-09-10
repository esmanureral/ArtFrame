package com.esmanureral.artframe.presentation.artworkdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esmanureral.artframe.data.network.ApiService
import com.esmanureral.artframe.presentation.artworkdetail.model.ArtworkDetailUI
import com.esmanureral.artframe.presentation.artworkdetail.model.toUIModel
import kotlinx.coroutines.launch

class ArtWorkDetailViewModel(private val apiService: ApiService) : ViewModel() {

    private val _artworkDetail = MutableLiveData<ArtworkDetailUI?>()
    val artworkDetail: LiveData<ArtworkDetailUI?> get() = _artworkDetail

    fun fetchArtworkDetail(id: Int) {
        viewModelScope.launch {
            val response = apiService.getArtworkDetail(id)
            if (response.isSuccessful) {
                val artwork = response.body()?.data
                artwork?.let { artworkData ->
                    val artistResponse = apiService.getArtistDetail(artworkData.artistId)
                    if (artistResponse.isSuccessful) {
                        val artist = artistResponse.body()?.data

                        _artworkDetail.value = artworkData.toUIModel(
                            birthDate = artist?.birthDate,
                            deathDate = artist?.deathDate
                        )
                    } else {
                        _artworkDetail.value = artworkData.toUIModel()
                    }
                }
            } else {
                _artworkDetail.value = null
            }
        }
    }
}
