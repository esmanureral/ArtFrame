package com.esmanureral.artframe.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.esmanureral.artframe.data.network.CollectionArtwork
import com.esmanureral.artframe.data.network.CorrectAnswer
import com.esmanureral.artframe.presentation.artistlist.model.ArtistListUI
import com.esmanureral.artframe.presentation.artworkdetail.model.ArtworkDetailUI
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val PREFS_NAME = "art_work"
private const val FAVORITES_ARTWORK_KEY = "favorites_artwork_list"
private const val FAVORITE_ARTISTS_KEY = "favorites_artist_list"
private const val ANIMATION_KEY = "appbar_animation_seen"
private const val CORRECT_ANSWERS_KEY = "correct_answers_list"
private const val QUESTION_INDEX_KEY = "question_index"
private const val POPULAR_ARTWORK__KEY = "popular_artworks"
private const val POPULAR_ARTIST__KEY = "hasPopularArtistsFetched"

class ArtWorkSharedPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun saveArtworkFavorites(favorites: MutableList<ArtworkDetailUI>) {
        val json = Gson().toJson(favorites)
        prefs.edit().putString(FAVORITES_ARTWORK_KEY, json).apply()
    }

    fun loadArtworkFavorites(): MutableList<ArtworkDetailUI> {
        val json = prefs.getString(FAVORITES_ARTWORK_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<ArtworkDetailUI>>() {}.type
            Gson().fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    fun addArtworkFavorite(artwork: ArtworkDetailUI) {
        val favorites = loadArtworkFavorites()
        if (favorites.none { it.id == artwork.id }) {
            favorites.add(artwork)
            saveArtworkFavorites(favorites)
        }
    }

    fun isArtworkFavorite(artwork: ArtworkDetailUI): Boolean {
        val favorites = loadArtworkFavorites()
        return favorites.any { it.id == artwork.id }
    }

    private fun saveArtistFavorites(favorites: MutableList<ArtistListUI>) {
        val json = Gson().toJson(favorites)
        prefs.edit().putString(FAVORITE_ARTISTS_KEY, json).apply()
    }

    fun loadArtistFavorites(): MutableList<ArtistListUI> {
        val json = prefs.getString(FAVORITE_ARTISTS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<ArtistListUI>>() {}.type
            Gson().fromJson(json, type)
        } else mutableListOf()
    }

    fun addArtistFavorite(artist: ArtistListUI) {
        val favorites = loadArtistFavorites()
        if (favorites.none { it.id == artist.id }) {
            favorites.add(artist)
            saveArtistFavorites(favorites)
        }
    }

    fun removeArtistById(artistId: Int) {
        val favorites = loadArtistFavorites()
        val newList = favorites.filter { it.id != artistId }.toMutableList()
        saveArtistFavorites(newList)
    }

    fun removeArtworkById(artworkId: Int) {
        val favorites = loadArtworkFavorites()
        val newList = favorites.filter { it.id != artworkId }.toMutableList()
        saveArtworkFavorites(newList)
    }

    fun removeAllArtists() {
        prefs.edit { remove(FAVORITE_ARTISTS_KEY) }
    }

    fun removeAllArtworks() {
        prefs.edit { remove(FAVORITES_ARTWORK_KEY) }
    }

    fun isArtistFavorite(artist: ArtistListUI): Boolean {
        return loadArtistFavorites().any { it.id == artist.id }
    }

    fun isAppBarAnimationSeen(): Boolean {
        return prefs.getBoolean(ANIMATION_KEY, false)
    }

    fun setAppBarAnimationSeen() {
        prefs.edit().putBoolean(ANIMATION_KEY, true).apply()
    }

    fun saveCorrectAnswers(correctAnswers: List<CorrectAnswer>) {
        val json = Gson().toJson(correctAnswers)
        prefs.edit().putString(CORRECT_ANSWERS_KEY, json).apply()
    }

    fun loadCorrectAnswers(): MutableList<CorrectAnswer> {
        val json = prefs.getString(CORRECT_ANSWERS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<CorrectAnswer>>() {}.type
            Gson().fromJson(json, type)
        } else mutableListOf()
    }

    fun saveQuestionIndex(index: Int) {
        prefs.edit().putInt(QUESTION_INDEX_KEY, index).apply()
    }

    fun loadQuestionIndex(): Int {
        return prefs.getInt(QUESTION_INDEX_KEY, 1)
    }

    fun savePopularArtworks(list: List<CollectionArtwork>) {
        val json = Gson().toJson(list)
        prefs.edit { putString(POPULAR_ARTWORK__KEY, json) }
        if (list.isNotEmpty()) {
            prefs.edit { putBoolean(POPULAR_ARTIST__KEY, true) }
        }
    }

    fun loadPopularArtworks(): List<CollectionArtwork> {
        val json = prefs.getString(POPULAR_ARTWORK__KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<CollectionArtwork>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun hasPopularArtistsFetched(): Boolean {
        return prefs.getBoolean(POPULAR_ARTIST__KEY, false)
    }
}