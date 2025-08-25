package com.esmanureral.artframe.presentation.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.esmanureral.artframe.DeleteBottomSheet
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.databinding.ItemFavArtworksBinding
import com.esmanureral.artframe.presentation.artworkdetail.model.ArtworkDetailUI

class FavoritesAdapter(
    private val favoritesPrefs: ArtWorkSharedPreferences,
    private val favorites: MutableList<ArtworkDetailUI>,
    private val onItemClick: (ArtworkDetailUI) -> Unit,
    private val onFavoritesChanged: () -> Unit = {},
    private val parentFragment: Fragment? = null
) : RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(val binding: ItemFavArtworksBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(artwork: ArtworkDetailUI) {
            with(binding) {
                tvTitle.text = artwork.title
                val imageUrl =
                    root.context.getString(R.string.artwork_image_url, artwork.imageId)
                ivArtwork.load(imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.black)
                    error(R.drawable.error)
                }
                ivFavorite.setImageResource(
                    if (favoritesPrefs.isArtworkFavorite(artwork)) R.drawable.favorite_24
                    else R.drawable.favorite_border
                )

                root.setOnClickListener {
                    onItemClick(artwork)
                }

                root.setOnLongClickListener {
                    showDeleteBottomSheet(artwork)
                    true
                }
            }
        }

        private fun showDeleteBottomSheet(artwork: ArtworkDetailUI) {
            val bottomSheet = DeleteBottomSheet()
            bottomSheet.setListener(object : DeleteBottomSheet.DeleteListener {
                override fun onDeleteItem() {
                    favoritesPrefs.removeArtworkById(artwork.id)
                    val position = favorites.indexOf(artwork)
                    if (position != -1) {
                        favorites.removeAt(position)
                        notifyItemRemoved(position)
                        onFavoritesChanged()
                    }
                }

                override fun onDeleteAll() {
                    favoritesPrefs.removeAllArtworks()
                    favorites.clear()
                    notifyDataSetChanged()
                    onFavoritesChanged()
                }
            })
            parentFragment?.let { fragment ->
                bottomSheet.show(fragment.parentFragmentManager, "DeleteBottomSheet")
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
