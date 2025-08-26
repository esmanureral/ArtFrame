package com.esmanureral.artframe.presentation.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.data.network.ApiClient
import com.esmanureral.artframe.data.network.ApiService
import com.esmanureral.artframe.data.network.Artwork
import com.esmanureral.artframe.data.network.CorrectAnswer
import com.esmanureral.artframe.data.network.QuizQuestion
import com.esmanureral.artframe.presentation.artworkdetail.model.toUIModel
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

    init {
        loadArtists()
    }

    private fun loadArtists() {
        if (!_allArtists.value.isNullOrEmpty()) return

        viewModelScope.launch {
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
        }
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
            val response = api.getArtWorks(page = randomPage, limit = 20)

            if (response.isSuccessful) {
                val artworks = response.body()?.data.orEmpty()
                val validArtworks = artworks.filter { !it.imageId.isNullOrBlank() }

                if (validArtworks.isNotEmpty()) {
                    val randomArtwork = validArtworks.random()
                    createQuizQuestion(randomArtwork)
                } else {
                    _error.value = true
                }
            } else {
                _error.value = true
            }
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
    }

    fun resetQuiz() {
        correctAnswersList.clear()
        _correctAnswers.value = emptyList()
        _quizQuestion.value = null
        _error.value = false
    }

    fun getScore(): Int = correctAnswersList.size
}