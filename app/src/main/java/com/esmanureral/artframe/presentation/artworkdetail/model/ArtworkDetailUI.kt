package com.esmanureral.artframe.presentation.artworkdetail.model

import com.esmanureral.artframe.data.network.ArtworkDetail
import com.esmanureral.artframe.data.network.Thumbnail

data class ArtworkDetailUI(
    val id: Int,
    val title: String,
    val artistTitle: String,
    val dateDisplay: String,
    val description: String?,
    val shortDescription: String,
    val imageId: String,
    val artistId: Int,
    val thumbnail: Thumbnail?,
    val dimension: String,
    val creditLine: String
)

fun ArtworkDetail.toUIModel() = ArtworkDetailUI(
    id = this.id, title = title.orEmpty(),
    artistTitle = artistTitle.orEmpty(),
    dateDisplay = dateDisplay.orEmpty(),
    description = description.orEmpty(),
    shortDescription = shortDescription.orEmpty(),
    imageId = imageId.orEmpty(),
    artistId = artistId,
    thumbnail = thumbnail,
    dimension = dimensions.orEmpty(),
    creditLine = creditLine.orEmpty()
)