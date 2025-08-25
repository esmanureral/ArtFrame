package com.esmanureral.artframe.presentation.artistlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.esmanureral.artframe.DeleteBottomSheet
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.databinding.ItemArtistBinding
import com.esmanureral.artframe.presentation.artistlist.model.ArtistListUI
import com.esmanureral.artframe.presentation.deleteItem.DeleteItemType

class ArtistListAdapter(
    private val favoritesPrefs: ArtWorkSharedPreferences,
    private val onItemClick: (ArtistListUI) -> Unit,
    private val isRemoveFavorite: Boolean = false,
    private val parentFragment: Fragment? = null
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

            // Kalbe basıldığında bottom sheet açma (favoriler sayfasında)
            if (isRemoveFavorite) {
                ivFavorite.setOnClickListener { 
                    showDeleteBottomSheet(artist, DeleteItemType.ARTIST)
                }
            } else {
                ivFavorite.setOnClickListener { toggleFavorite(artist) }
            }
            
            root.setOnClickListener { onItemClick(artist) }

            // Uzun basma ile bottom sheet açma (sadece favoriler sayfasında)
            if (isRemoveFavorite) {
                root.setOnLongClickListener {
                    showDeleteBottomSheet(artist, DeleteItemType.ARTIST)
                    true
                }
            }
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

        private fun showDeleteBottomSheet(artist: ArtistListUI, itemType: DeleteItemType) {
            val bottomSheet = DeleteBottomSheet()
            bottomSheet.setListener(object : DeleteBottomSheet.DeleteListener {
                override fun onDeleteItem() {
                    when (itemType) {
                        DeleteItemType.ARTWORK -> {
                            favoritesPrefs.removeArtworkById(artworkId = artist.id)
                        }
                        DeleteItemType.ARTIST -> {
                            favoritesPrefs.removeArtistById(artistId = artist.id)
                        }
                    }
                    
                    val position = currentList.indexOf(artist)
                    if (position != -1) {
                        val newList = currentList.toMutableList()
                        newList.removeAt(position)
                        submitList(newList)
                    }
                }

                override fun onDeleteAll() {
                    when (itemType) {
                        DeleteItemType.ARTWORK -> {
                            favoritesPrefs.removeAllArtworks()
                        }
                        DeleteItemType.ARTIST -> {
                            favoritesPrefs.removeAllArtists()
                        }
                    }
                    
                    submitList(emptyList())
                }
            })
            parentFragment?.let { fragment ->
                bottomSheet.show(fragment.parentFragmentManager, "DeleteBottomSheet")
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
