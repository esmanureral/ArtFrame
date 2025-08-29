package com.esmanureral.artframe.data.network

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api.artic.edu/api/v1/"

    fun getApi(context: Context): ApiService {
        return provideRetrofit(context).create(ApiService::class.java)
    }

    private fun provideOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                ChuckerInterceptor.Builder(context)
                    .build()
            )
            .build()
    }

    private fun provideRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}