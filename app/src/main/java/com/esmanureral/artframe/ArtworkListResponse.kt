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
data class ArtworkDetailResponse(
    val data: ArtworkDetail
)

data class ArtworkDetail(
    val id: Int,
    val title: String?,
    val artist_display: String?,
    val date_display: String?,
    val medium_display: String?,
    val dimensions: String?,
    val credit_line: String?,
    val description: String?,
    val short_description: String?,
    val image_id: String?
)
