package com.esmanureral.artframe.presentation.artistlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.databinding.ItemArtistBinding
import com.esmanureral.artframe.presentation.artistlist.model.ArtistListUI

class ArtistListAdapter(
    private val favoritesPrefs: ArtWorkSharedPreferences,
    private val onItemClick: (ArtistListUI) -> Unit,
    private val isRemoveFavorite: Boolean = false
) : ListAdapter<ArtistListUI, ArtistListAdapter.ArtistViewHolder>(ArtistDiffCallback()) {

    inner class ArtistViewHolder(private val binding: ItemArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(artist: ArtistListUI) = with(binding) {
            val context = root.context
            tvArtistName.text = artist.title
            val birth = artist.birthDate ?: "?"
            val death = artist.deathDate ?: "?"
            tvYears.text = context.getString(R.string.artist_years, birth, death)

            updateFavoriteIcon(artist)

            ivFavorite.setOnClickListener { toggleFavorite(artist) }
            root.setOnClickListener { onItemClick(artist) }
        }

        private fun updateFavoriteIcon(artist: ArtistListUI) {
            binding.ivFavorite.setImageResource(
                if (favoritesPrefs.isArtistFavorite(artist)) R.drawable.favorite_24
                else R.drawable.favorite_border
            )
        }

        private fun toggleFavorite(artist: ArtistListUI) = with(binding) {
            if (favoritesPrefs.isArtistFavorite(artist)) {
                favoritesPrefs.removeArtistById(artistId = artist.id)
                if (isRemoveFavorite) removeArtistFromList(adapterPosition)
                else ivFavorite.setImageResource(R.drawable.favorite_border)
            } else {
                favoritesPrefs.addArtistFavorite(artist)
                ivFavorite.setImageResource(R.drawable.favorite_24)
            }
        }

        private fun removeArtistFromList(position: Int) {
            if (position != RecyclerView.NO_POSITION) {
                val newList = currentList.toMutableList()
                newList.removeAt(position)
                submitList(newList)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val binding = ItemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ArtistDiffCallback : DiffUtil.ItemCallback<ArtistListUI>() {
        override fun areItemsTheSame(oldItem: ArtistListUI, newItem: ArtistListUI) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ArtistListUI, newItem: ArtistListUI) =
            oldItem == newItem
    }
}
