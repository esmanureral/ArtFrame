package com.esmanureral.artframe.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("artworks")
    suspend fun getArtWorks(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 100,
        @Query("fields") fields: String = "id,title,image_id,classification_title"
    ): Response<ArtworkListResponse>

    @GET("artworks/{id}")
    suspend fun getArtworkDetail(
        @Path("id") id: Int
    ): Response<ArtworkDetailResponse>

    @GET("artists")
    suspend fun getArtists(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 20,
        @Query("fields") fields: String = "id,title,birth_date,death_date"
    ): Response<ArtistsListResponse>

    @GET("artists/{id}")
    suspend fun getArtistDetail(
        @Path("id") id: Int,
        @Query("fields") fields: String = "id,title,birth_date,death_date"
    ): Response<ArtistDetailResponse>

    @GET("artworks/search")
    suspend fun getArtworksByArtist(
        @Query("query[term][artist_ids]") artistId: Int,
        @Query("limit") limit: Int = 20,
        @Query("fields") fields: String = "id,title,image_id"
    ): Response<ArtworkListResponse>
}