package com.esmanureral.artframe.presentation.game

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.data.network.ApiClient
import com.esmanureral.artframe.data.network.ApiService
import com.esmanureral.artframe.data.network.Artwork
import com.esmanureral.artframe.data.network.CollectionArtwork
import com.esmanureral.artframe.data.network.CorrectAnswer
import com.esmanureral.artframe.data.network.QuizQuestion
import com.esmanureral.artframe.presentation.artworkdetail.model.toUIModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val api: ApiService by lazy { ApiClient.getApi(getApplication()) }

    private val _allArtists = MutableLiveData<List<String>>()
    val allArtists: LiveData<List<String>> get() = _allArtists

    private val _quizQuestion = MutableLiveData<QuizQuestion?>()
    val quizQuestion: LiveData<QuizQuestion?> get() = _quizQuestion

    private val _error = MutableLiveData(false)
    val error: LiveData<Boolean> get() = _error

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _correctAnswers = MutableLiveData<List<CorrectAnswer>>(emptyList())
    val correctAnswers: LiveData<List<CorrectAnswer>> get() = _correctAnswers

    private val correctAnswersList = mutableListOf<CorrectAnswer>()

    private val _answeredQuestions = mutableMapOf<String, String?>()
    val answeredQuestions: Map<String, String?> get() = _answeredQuestions

    private val _popularArtworks = MutableLiveData<List<CollectionArtwork>>(emptyList())
    val popularArtworks: LiveData<List<CollectionArtwork>> get() = _popularArtworks

    private val popularArtworksList = mutableListOf<CollectionArtwork>()

    val isCollectionsReady = MutableLiveData(false)

    private val popularArtistIds = listOf(
        36198, 35397, 40669, 40610, 4298, 44014, 37343, 34123, 14096, 35809, 33808, 33710
    )

    init {
        loadCorrectAnswersFromPrefs()
        loadArtists()
    }

    private fun loadArtists() {
        if (!_allArtists.value.isNullOrEmpty()) return

        viewModelScope.launch {
            try {
                val response = api.getArtists(page = 1, limit = 100)
                if (response.isSuccessful) {
                    val artists = response.body()?.data
                        ?.mapNotNull { it.title }
                        ?.filter { it.isNotBlank() && it != "Unknown" && it != "Anonymous" }
                        ?.distinct()
                        ?.take(100) ?: emptyList()

                    _allArtists.value = artists
                    loadNewQuestion()
                } else {
                    _error.value = true
                }
            } catch (e: Exception) {
                Log.e("LoadArtistsError", "Error loading artists", e)
                _error.value = true
            }
        }
    }

    fun loadPopularArtworks() {
        viewModelScope.launch {
            fetchPopularArtworksFromApi()
        }
    }

    private suspend fun fetchPopularArtworksFromApi() {
        popularArtworksList.clear()

        for (artistId in popularArtistIds) {
            val response = api.getArtworksByArtist(artistId, limit = 20)
            if (response.isSuccessful) {
                response.body()?.data?.forEach { artwork ->
                    if (!artwork.imageId.isNullOrBlank()) {
                        popularArtworksList.add(buildCollectionArtwork(artwork, artistId))
                    }
                }
            }
            delay(100)
        }
        isCollectionsReady.value = true
    }

    private fun buildCollectionArtwork(artwork: Artwork, artistId: Int): CollectionArtwork {
        val price = (10_000..10_000_000).random().toDouble()
        val imageUrl = getApplication<Application>().getString(
            R.string.artwork_image_url,
            artwork.imageId
        )
        return CollectionArtwork(
            artworkId = artwork.id,
            artistId = artistId,
            price = price,
            isOwned = false,
            imageUrl = imageUrl
        )
    }

    fun startQuiz() {
        if (_allArtists.value.isNullOrEmpty()) {
            loadArtists()
        } else {
            loadNewQuestion()
        }
    }

    fun loadNewQuestion() {
        val artists = _allArtists.value ?: return
        if (artists.isEmpty()) return

        _isLoading.value = true
        _error.value = false

        viewModelScope.launch {
            val randomPage = (1..1000).random()
            val response = api.getArtWorks(page = randomPage, limit = 100)

            if (response.isSuccessful) {
                val artworks = response.body()?.data.orEmpty()
                val filteredArtworks = filterArtworks(artworks, getIncludedClassifications())

                if (filteredArtworks.isNotEmpty()) {
                    val randomArtwork = filteredArtworks.random()
                    createQuizQuestion(randomArtwork)
                } else {
                    _error.value = true
                }
            } else {
                _error.value = true
            }

            _isLoading.value = false
        }
    }

    private fun filterArtworks(
        artworks: List<Artwork>,
        includedClassifications: List<String>
    ): List<Artwork> {
        val allowed = includedClassifications.map { it.lowercase() }
        return artworks.filter { !it.imageId.isNullOrBlank() }
            .filter { artwork ->
                val title = artwork.classificationTitle?.trim()?.lowercase().orEmpty()
                allowed.any { title.contains(it) }
            }
    }

    private suspend fun createQuizQuestion(artwork: Artwork) {
        val detailResponse = api.getArtworkDetail(artwork.id)
        if (detailResponse.isSuccessful) {
            detailResponse.body()?.data?.toUIModel()?.let { artworkDetail ->
                val artist = artworkDetail.artistTitle
                if (artist.isBlank() || artist == "Unknown" || artist == "Anonymous") {
                    loadNewQuestion()
                    return
                }

                val imageUrl = getApplication<Application>().getString(
                    R.string.artwork_image_url, artworkDetail.imageId
                )

                val options = generateOptions(artist)

                _quizQuestion.value = QuizQuestion(
                    artworkId = artwork.id.toString(),
                    imageUrl = imageUrl,
                    correctAnswer = artist,
                    options = options
                )

                _isLoading.value = false
            } ?: run { _error.value = true }
        } else {
            _error.value = true
        }
    }

    private fun generateOptions(correctAnswer: String): List<String> {
        val currentArtists = _allArtists.value ?: emptyList()
        val wrongArtists = currentArtists.filter { it != correctAnswer }.shuffled().take(2)
        return (listOf(correctAnswer) + wrongArtists).shuffled()
    }

    fun onCorrectAnswer(question: QuizQuestion) {
        val correctAnswer = CorrectAnswer(
            artworkId = question.artworkId,
            imageUrl = question.imageUrl,
            artistName = question.correctAnswer
        )
        correctAnswersList.add(correctAnswer)
        _correctAnswers.value = correctAnswersList.toList()

        ArtWorkSharedPreferences(getApplication()).saveCorrectAnswers(correctAnswersList)

        updateOwnedArtwork(question.artworkId)
    }

    private fun updateOwnedArtwork(artworkId: String) {
        val prefs = ArtWorkSharedPreferences(getApplication())
        val artworks = prefs.loadPopularArtworks().toMutableList()

        val index = artworks.indexOfFirst { it.artworkId.toString() == artworkId }
        if (index != -1) {
            val updated = artworks[index].copy(isOwned = true)
            artworks[index] = updated
            prefs.savePopularArtworks(artworks)
            _popularArtworks.value = artworks
        }
    }

    private fun loadCorrectAnswersFromPrefs() {
        val savedAnswers = ArtWorkSharedPreferences(getApplication()).loadCorrectAnswers()
        correctAnswersList.addAll(savedAnswers)
        _correctAnswers.value = correctAnswersList.toList()
    }

    fun resetQuiz() {
        correctAnswersList.clear()
        _correctAnswers.value = emptyList()
        _quizQuestion.value = null
        _error.value = false
    }

    fun recordAnswer(artworkId: String, answer: String) {
        _answeredQuestions[artworkId] = answer
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