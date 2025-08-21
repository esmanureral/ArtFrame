package com.esmanureral.artframe.presentation.artistlist.model

import com.esmanureral.artframe.data.network.Artists

data class ArtistListUI(
    val id: Int,
    val title: String,
    val birthDate: String?,
    val deathDate: String?
)

fun Artists.toUIModel() = ArtistListUI(
    id = id,
    title = title.orEmpty(),
    birthDate = birthDate,
    deathDate = deathDate
)