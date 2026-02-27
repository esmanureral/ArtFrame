package com.esmanureral.artframe

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.data.network.ApiClient
import com.esmanureral.artframe.data.network.ApiService
import com.google.android.gms.ads.MobileAds

class ArtFrameApplication : Application(), ImageLoaderFactory {

    lateinit var apiService: ApiService
        private set

    lateinit var sharedPreferences: ArtWorkSharedPreferences
        private set

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
        apiService = ApiClient.getApi(this)
        sharedPreferences = ArtWorkSharedPreferences(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .crossfade(true)
            .build()
    }
}
