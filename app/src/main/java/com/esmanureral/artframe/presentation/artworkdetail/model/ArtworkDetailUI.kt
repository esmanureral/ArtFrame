package com.esmanureral.artframe.presentation.artworkdetail.model

import com.esmanureral.artframe.data.network.ArtworkDetail
import com.esmanureral.artframe.data.network.Thumbnail

data class ArtworkDetailUI(
    val id: Int,
    val title: String,
    val artistTitle: String,
    val birthDate: String?,
    val deathDate: String?,
    val dateDisplay: String,
    val description: String?,
    val shortDescription: String,
    val imageId: String,
    val artistId: Int,
    val thumbnail: Thumbnail?,
    val dimension: String,
    val creditLine: String,
    val artistDisplay: String,
    val placeOfOrigin: String
)

fun ArtworkDetail.toUIModel(
    birthDate: String? = null,
    deathDate: String? = null
) = ArtworkDetailUI(
    id = this.id,
    title = title.orEmpty(),
    artistTitle = artistTitle.orEmpty(),
    birthDate = birthDate,
    deathDate = deathDate,
    dateDisplay = dateDisplay.orEmpty(),
    description = description,
    shortDescription = shortDescription.orEmpty(),
    imageId = imageId.orEmpty(),
    artistId = artistId,
    thumbnail = thumbnail,
    dimension = dimensions.orEmpty(),
    creditLine = creditLine.orEmpty(),
    artistDisplay = artistDisplay.orEmpty(),
    placeOfOrigin = placeOfOrigin.orEmpty()
)
