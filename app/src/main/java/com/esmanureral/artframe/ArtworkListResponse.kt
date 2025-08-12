package com.esmanureral.artframe

import com.google.gson.annotations.SerializedName

data class ArtworkListResponse(
    val data: List<Artwork>
)

data class Artwork(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("artist_display")
    val artistDisplay: String?,
    @SerializedName("image_id")
    val imageId: String?
)
