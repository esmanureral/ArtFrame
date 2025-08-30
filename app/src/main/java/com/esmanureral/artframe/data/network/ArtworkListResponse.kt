package com.esmanureral.artframe.data.network

import com.google.gson.annotations.SerializedName

data class ArtworkListResponse(
    val data: List<Artwork>
)

data class Artwork(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("image_id")
    val imageId: String?,
    @SerializedName("classification_title")
    val classificationTitle: String?,
    @SerializedName("artist_title")
    val artistTitle: String?,
)
