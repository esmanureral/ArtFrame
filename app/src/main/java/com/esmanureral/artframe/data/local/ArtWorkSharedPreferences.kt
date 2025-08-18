package com.esmanureral.artframe.data.local

import android.content.Context
import android.content.SharedPreferences
import com.esmanureral.artframe.data.network.Artists
import com.esmanureral.artframe.presentation.artworkdetail.model.ArtworkDetailUI
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val PREFS_NAME = "art_work"
private const val FAVORITES_ARTWORK_KEY = "favorites_artwork_list"
private const val FAVORITE_ARTISTS_KEY = "favorites_artist_list"

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

    fun removeArtworkFavorite(artwork: ArtworkDetailUI) {
        val favorites = loadArtworkFavorites()
        val newList = favorites.filter { it.id != artwork.id }.toMutableList()
        saveArtworkFavorites(newList)
    }

    fun isArtworkFavorite(artwork: ArtworkDetailUI): Boolean {
        val favorites = loadArtworkFavorites()
        return favorites.any { it.id == artwork.id }
    }

    private fun saveArtistFavorites(favorites: MutableList<Artists>) {
        val json = Gson().toJson(favorites)
        prefs.edit().putString(FAVORITE_ARTISTS_KEY, json).apply()
    }

    fun loadArtistFavorites(): MutableList<Artists> {
        val json = prefs.getString(FAVORITE_ARTISTS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Artists>>() {}.type
            Gson().fromJson(json, type)
        } else mutableListOf()
    }

    fun addArtistFavorite(artist: Artists) {
        val favorites = loadArtistFavorites()
        if (favorites.none { it.id == artist.id }) {
            favorites.add(artist)
            saveArtistFavorites(favorites)
        }
    }

    fun removeArtistFavorite(artist: Artists) {
        val favorites = loadArtistFavorites()
        val newList = favorites.filter { it.id != artist.id }.toMutableList()
        saveArtistFavorites(newList)
    }

    fun isArtistFavorite(artist: Artists): Boolean {
        return loadArtistFavorites().any { it.id == artist.id }
    }
}