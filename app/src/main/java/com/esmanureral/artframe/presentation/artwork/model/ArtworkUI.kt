package com.esmanureral.artframe.presentation.artwork.model

import com.esmanureral.artframe.data.network.Artwork


data class ArtworkUI(
    val id: Int,
    val title: String,
    val imageId: String,
    val classificationTitle: String?
)

fun Artwork.toUIModel() = ArtworkUI(
    id = id,
    title = title.orEmpty(),
    imageId = imageId.orEmpty(),
    classificationTitle = classificationTitle.orEmpty()
)