package com.esmanureral.artframe

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("artworks")
    suspend fun getArtWorks(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("fields") fields: String = "id,title,artist_display,image_id"
    ): Response<ArtworkListResponse>
}