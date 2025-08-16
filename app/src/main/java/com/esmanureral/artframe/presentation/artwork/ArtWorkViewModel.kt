package com.esmanureral.artframe.presentation.artwork

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.esmanureral.artframe.data.network.ApiClient
import com.esmanureral.artframe.data.network.ApiService
import com.esmanureral.artframe.data.network.Artwork

class ArtWorkViewModel(application: Application) : AndroidViewModel(application) {

    private val api: ApiService by lazy {
        ApiClient.getApi(getApplication())
    }

    private val _artworks = MutableLiveData<List<Artwork>>()
    val artworks: LiveData<List<Artwork>> get() = _artworks

    private val allArtworks = mutableListOf<Artwork>()
    private var currentPage = 1
    private var isLoading = false

    init {
        fetchArtworks()
    }

    fun fetchArtworks() {
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            val response = api.getArtWorks(page = currentPage)
            val includedClassifications =
                listOf(
                    "etching",
                    "oil on canvas",
                    "ink or chalk wash",
                    "salted paper print",
                    "graphite",
                    "painting",
                    "woodblock print",
                    "lithograph",
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
                    "prints and drawing",
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
            if (response.isSuccessful) {
                val newData = response.body()?.data
                    ?.filter { !it.imageId.isNullOrBlank() }
                    ?.filter { it.classificationTitle in includedClassifications }
                    ?: emptyList()
                allArtworks.addAll(newData)
                _artworks.postValue(newData)
                currentPage++
            }
            isLoading = false
        }
    }
}