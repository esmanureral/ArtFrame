package com.esmanureral.artframe.presentation.artistlist

import android.content.Context
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
            tvArtistName.text = artist.title
            tvYears.text = formatArtistYears(context = root.context, artist = artist)
            updateFavoriteIcon(artist = artist)
            setupFavoriteClick(artist = artist)
            setupRootClick(artist = artist)
            setupRootLongClick(artist = artist)
        }

        private fun setupFavoriteClick(artist: ArtistListUI) = with(binding) {
            if (isRemoveFavorite) {
                ivFavorite.setOnClickListener {
                    showDeleteBottomSheet(artist = artist, itemType = DeleteItemType.ARTIST)
                }
            } else {
                toggleFavorite(artist = artist)
            }
        }

        private fun setupRootClick(artist: ArtistListUI) = with(binding) {
            root.setOnClickListener { onItemClick(artist) }
        }

        private fun setupRootLongClick(artist: ArtistListUI) = with(binding) {
            if (isRemoveFavorite) {
                root.setOnLongClickListener {
                    showDeleteBottomSheet(artist = artist, itemType = DeleteItemType.ARTIST)
                    true
                }
            }
        }

        private fun formatArtistYears(context: Context, artist: ArtistListUI): String {
            val birth = artist.birthDate ?: "?"
            val death = artist.deathDate ?: "?"
            return context.getString(R.string.artist_years, birth, death)
        }

        private fun updateFavoriteIcon(artist: ArtistListUI) {
            binding.ivFavorite.setImageResource(
                if (favoritesPrefs.isArtistFavorite(artist)) R.drawable.favorite_24
                else R.drawable.favorite_border
            )
        }

        private fun toggleFavorite(artist: ArtistListUI) {
            if (favoritesPrefs.isArtistFavorite(artist)) {
                favoritesPrefs.removeArtistById(artist.id)
            } else {
                favoritesPrefs.addArtistFavorite(artist)
            }
            updateFavoriteIcon(artist)
        }

        private fun showDeleteBottomSheet(artist: ArtistListUI, itemType: DeleteItemType) {
            val bottomSheet = DeleteBottomSheet()
            bottomSheet.setListener(object : DeleteBottomSheet.DeleteListener {
                override fun onDeleteItem() {
                    handleSingleDelete(artist = artist, itemType = itemType)
                }

                override fun onDeleteAll() {
                    handleDeleteAll(itemType = itemType)
                }
            })
            parentFragment?.let { fragment ->
                bottomSheet.show(fragment.parentFragmentManager, "DeleteBottomSheet")
            }
        }

        private fun handleSingleDelete(artist: ArtistListUI, itemType: DeleteItemType) {
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

        private fun handleDeleteAll(itemType: DeleteItemType) {
            when (itemType) {
                DeleteItemType.ARTWORK -> favoritesPrefs.removeAllArtworks()
                DeleteItemType.ARTIST -> favoritesPrefs.removeAllArtists()
            }
            submitList(emptyList())
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
