package com.esmanureral.artframe

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val PREFS_NAME = "art_work"
private const val FAVORITES_KEY = "favorites_list"

class ArtWorkSharedPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val gson = Gson()

    private fun saveFavorites(favorites: MutableList<ArtworkDetail>) {
        val json = gson.toJson(favorites)
        prefs.edit().putString(FAVORITES_KEY, json).apply()
    }

    fun loadFavorites(): MutableList<ArtworkDetail> {
        val json = prefs.getString(FAVORITES_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<ArtworkDetail>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    fun addFavorite(artwork: ArtworkDetail) {
        val favorites = loadFavorites()
        if (favorites.none { it.id == artwork.id }) {
            favorites.add(artwork)
            saveFavorites(favorites)
        }
    }

    fun removeFavorite(artwork: ArtworkDetail) {
        val favorites = loadFavorites()
        val newList = favorites.filter { it.id != artwork.id }.toMutableList()
        saveFavorites(newList)
    }

    fun isFavorite(artwork: ArtworkDetail): Boolean {
        val favorites = loadFavorites()
        return favorites.any { it.id == artwork.id }
    }
}