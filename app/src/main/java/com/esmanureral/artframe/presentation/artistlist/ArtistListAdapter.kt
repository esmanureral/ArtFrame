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
        fun bind(artists: ArtistListUI) {
            val context = binding.root.context
            with(binding) {
                tvArtistName.text = artists.title
                val birth = artists.birthDate
                val death = artists.deathDate
                tvYears.text = context.getString(R.string.artist_years, birth, death)

                ivFavorite.setImageResource(
                    if (favoritesPrefs.isArtistFavorite(artists)) R.drawable.favorite_24
                    else R.drawable.favorite_border
                )
                ivFavorite.setOnClickListener {
                    if (favoritesPrefs.isArtistFavorite(artists)) {
                        favoritesPrefs.removeArtistFavorite(artists)
                        if (isRemoveFavorite) {
                            val newList = currentList.toMutableList()
                            newList.removeAt(adapterPosition)
                            submitList(newList)
                        } else {
                            ivFavorite.setImageResource(R.drawable.favorite_border)
                        }
                    } else {
                        favoritesPrefs.addArtistFavorite(artists)
                        ivFavorite.setImageResource(R.drawable.favorite_24)
                    }
                }
                root.setOnClickListener {
                    onItemClick(artists)
                }
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