package com.esmanureral.artframe.data.network

import com.google.gson.annotations.SerializedName

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