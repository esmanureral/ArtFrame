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
    @SerializedName("image_id")
    val imageId: String?
)

data class ArtworkDetailResponse(
    val data: ArtworkDetail
)

data class ArtworkDetail(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("artist_display")
    val artistDisplay: String?,
    @SerializedName("date_display")
    val dateDisplay: String?,
    @SerializedName("medium_display")
    val mediumDisplay: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("short_description")
    val shortDescription: String?,
    @SerializedName("image_id")
    val imageId: String?
)

data class ArtistsListResponse(
    val data: List<Artists>
)

data class Artists(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("birth_date")
    val birthDate: String?,
    @SerializedName("death_date")
    val deathDate: String?
)