package com.esmanureral.artframe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.esmanureral.artframe.databinding.ItemArtworkBinding

class FavoritesAdapter(
    private val favorites: List<ArtworkDetail>,
    private val onItemClick: (ArtworkDetail) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(val binding: ItemArtworkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(artwork: ArtworkDetail) {
            binding.title.text = artwork.title ?: "-"
            val imageUrl = "https://www.artic.edu/iiif/2/${artwork.imageId}/full/200,/0/default.jpg"
            binding.image.load(imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_foreground)
            }
            binding.root.setOnClickListener {
                onItemClick(artwork)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemArtworkBinding.inflate(inflater, parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favorites[position])
    }

    override fun getItemCount() = favorites.size
}
