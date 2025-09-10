package com.esmanureral.artframe

import android.app.Application
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.data.network.ApiClient
import com.esmanureral.artframe.data.network.ApiService

class ArtFrameApplication : Application() {

    lateinit var apiService: ApiService
        private set

    lateinit var sharedPreferences: ArtWorkSharedPreferences
        private set

    override fun onCreate() {
        super.onCreate()
        apiService = ApiClient.getApi(this)
        sharedPreferences = ArtWorkSharedPreferences(this)
    }
}
