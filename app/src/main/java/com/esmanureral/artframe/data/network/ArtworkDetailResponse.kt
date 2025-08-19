package com.esmanureral.artframe.data.network

import com.google.gson.annotations.SerializedName

data class ArtworkDetailResponse(
    val data: ArtworkDetail
)

data class ArtworkDetail(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("artist_title")
    val artistTitle: String?,
    @SerializedName("date_display")
    val dateDisplay: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("short_description")
    val shortDescription: String?,
    @SerializedName("image_id")
    val imageId: String?,
    @SerializedName("artist_id")
    val artistId: Int,
    @SerializedName("thumbnail")
    val thumbnail: Thumbnail?,
    @SerializedName("dimensions")
    val dimensions: String?,
    @SerializedName("credit_line")
    val creditLine: String?,
    @SerializedName("artist_display")
    val artistDisplay: String?,
    @SerializedName("place_of_origin")
    val placeOfOrigin: String?,
)

data class Thumbnail(
    @SerializedName("alt_text")
    val altText: String?
)