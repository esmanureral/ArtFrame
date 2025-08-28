package com.esmanureral.artframe.presentation.collection

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.network.CollectionArtwork
import com.esmanureral.artframe.databinding.ItemCollectionBinding
import com.esmanureral.artframe.loadWithIndicator
import java.util.Locale

class CollectionAdapter(
    private var artworks: List<CollectionArtwork>,
    private val onClick: (Int) -> Unit,
    private val onItemsNotFound: (List<Int>) -> Unit
) : RecyclerView.Adapter<CollectionAdapter.ArtworkViewHolder>() {

    private var notFoundItems: MutableList<Int> = mutableListOf()

    inner class ArtworkViewHolder(private val binding: ItemCollectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(artwork: CollectionArtwork) {

            val isLastItem = artworks.last().artworkId == artwork.artworkId

            loadArtworkImage(artwork = artwork, isLastItem = isLastItem)
            showFormattedPrice(artwork.price)
            applyOwnershipEffect(artwork.isOwned)
            if (artwork.isOwned) {
                binding.root.setOnClickListener {
                    onClick(artwork.artworkId)
                }
            }
        }

        private fun loadArtworkImage(artwork: CollectionArtwork, isLastItem: Boolean) =
            with(binding) {
                if (artwork.isUnknown) {
                    return@with
                }

                imgArtworkWon.loadWithIndicator(
                    url = artwork.imageUrl,
                    progressIndicator = progressIndicator,
                    errorRes = R.drawable.error,
                    onError = {
                        notFoundItems.add(artwork.artworkId)

                        if (isLastItem) {
                            if (notFoundItems.isNotEmpty()) {
                                onItemsNotFound(notFoundItems.toList())
                                notFoundItems.clear()
                            }
                        }
                    },
                    onSuccess = {
                        if (isLastItem) {
                            if (notFoundItems.isNotEmpty()) {
                                onItemsNotFound(notFoundItems.toList())
                                notFoundItems.clear()
                            }
                        }
                    })
            }

        private fun showFormattedPrice(price: Double) = with(binding) {
            val formattedPrice = String.format(Locale.US, "%,.0f", price)
            tvPrice.text = root.context.getString(R.string.artwork_price, formattedPrice)
        }

        private fun applyOwnershipEffect(isOwned: Boolean) = with(binding.imgArtworkWon) {
            if (!isOwned) {
                val matrix = ColorMatrix().apply { setSaturation(0f) }
                colorFilter = ColorMatrixColorFilter(matrix)
                alpha = 0.3f
            } else {
                colorFilter = null
                alpha = 1f
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtworkViewHolder {
        val binding =
            ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtworkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtworkViewHolder, position: Int) {
        holder.bind(artworks[position])
    }

    override fun getItemCount(): Int = artworks.size

    fun updateList(newList: List<CollectionArtwork>) {
        artworks = newList
        notifyDataSetChanged()
    }
}