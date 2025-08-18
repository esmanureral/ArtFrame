package com.esmanureral.artframe.presentation.artistlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.data.network.Artists
import com.esmanureral.artframe.databinding.ItemArtistBinding
import com.esmanureral.artframe.presentation.artistlist.model.ArtistListUI

class ArtistListAdapter(
    private val favoritesPrefs: ArtWorkSharedPreferences,
    private val onItemClick: (ArtistListUI) -> Unit,
    private val isRemoveFavorite: Boolean = false
) : RecyclerView.Adapter<ArtistListAdapter.ArtistViewHolder>() {
    private val artistItems = mutableListOf<ArtistListUI>()

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
                            artistItems.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                        } else {
                            notifyItemChanged(adapterPosition)
                        }
                    } else {
                        favoritesPrefs.addArtistFavorite(artists)
                        notifyItemChanged(adapterPosition)
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
        holder.bind(artistItems[position])
    }

    override fun getItemCount(): Int = artistItems.size

    fun addData(newArtists: List<ArtistListUI>) {
        val startPos = artistItems.size
        artistItems.addAll(newArtists)
        notifyItemRangeInserted(startPos, newArtists.size)
    }
}