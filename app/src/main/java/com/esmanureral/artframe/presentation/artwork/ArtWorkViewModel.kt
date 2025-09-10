package com.esmanureral.artframe.presentation.artwork

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.esmanureral.artframe.data.network.ApiService
import com.esmanureral.artframe.presentation.artwork.model.ArtworkUI
import com.esmanureral.artframe.presentation.artwork.model.toUIModel

class ArtWorkViewModel(private val apiService: ApiService) : ViewModel() {

    private val _artworks = MutableLiveData<List<ArtworkUI>>()
    val artworks: LiveData<List<ArtworkUI>> get() = _artworks

    private val allArtworks = mutableListOf<ArtworkUI>()
    private var currentPage = 1
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchArtworks()
    }

    fun fetchArtworks() {
        if (_isLoading.value == true) return
        _isLoading.value = true

        viewModelScope.launch {
            val response = apiService.getArtWorks(page = currentPage)
            val includedClassificationsLower = getIncludedClassifications().map { it.lowercase() }

            if (response.isSuccessful) {
                val newData = response.body()?.data
                    ?.filter { !it.imageId.isNullOrBlank() }
                    ?.filter { artwork ->
                        val classificationTitle =
                            artwork.classificationTitle?.trim()?.lowercase().orEmpty()
                        val isIncluded = includedClassificationsLower.any { allowed ->
                            classificationTitle.contains(allowed)
                        }
                        isIncluded
                    }
                    ?.map { it.toUIModel() }
                    ?: emptyList()

                allArtworks.addAll(newData)
                _artworks.postValue(allArtworks)
                currentPage++
            }

            _isLoading.postValue(false)
        }
    }

    private fun getIncludedClassifications(): List<String> {
        return listOf(
            "oil on canvas",
            "ink or chalk wash",
            "painting",
            "woodblock print",
            "watercolor",
            "engraving",
            "earthenware",
            "print",
            "relief",
            "woodcut",
            "ink with wash",
            "miniature painting",
            "book",
            "chalk",
            "monotype",
            "ink and wash",
            "linocut",
            "photograph",
            "textile",
            "oil on panel",
            "pen and ink",
            "silver-dye bleach",
            "charcoal",
            "modern and contemporary art",
            "pen and ink drawings",
            "gelatin silver",
            "screenprint"
        )
    }
}