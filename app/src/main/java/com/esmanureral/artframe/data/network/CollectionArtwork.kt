package com.esmanureral.artframe.data.network

data class CollectionArtwork(
    val artworkId: Int,
    val artistId: Int,
    val price: Double,
    var isOwned: Boolean,
    val imageUrl: String,
    val artistTitle: String,
    val isUnknown: Boolean = false
)
