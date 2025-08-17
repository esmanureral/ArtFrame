package com.esmanureral.artframe.presentation.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.data.network.ArtworkDetail
import com.esmanureral.artframe.databinding.ItemFavArtworksBinding

class FavoritesAdapter(
    private val favoritesPrefs: ArtWorkSharedPreferences,
    private val favorites: MutableList<ArtworkDetail>,
    private val onItemClick: (ArtworkDetail) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(val binding: ItemFavArtworksBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(artwork: ArtworkDetail) {
            with(binding) {
                tvTitle.text = artwork.title ?: "-"
                val imageUrl =
                    "https://www.artic.edu/iiif/2/${artwork.imageId}/full/!1280,720/0/default.jpg"
                ivArtwork.load(imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.black)
                    error(R.drawable.error)
                }
                ivFavorite.setImageResource(
                    if (favoritesPrefs.isArtworkFavorite(artwork)) R.drawable.favorite_24
                    else R.drawable.favorite_border
                )

                ivFavorite.setOnClickListener {
                    if (favoritesPrefs.isArtworkFavorite(artwork)) {
                        favoritesPrefs.removeArtworkFavorite(artwork)
                    } else {
                        favoritesPrefs.addArtworkFavorite(artwork)
                    }
                    favorites.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }

                root.setOnClickListener {
                    onItemClick(artwork)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFavArtworksBinding.inflate(inflater, parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favorites[position])
    }

    override fun getItemCount() = favorites.size
}
