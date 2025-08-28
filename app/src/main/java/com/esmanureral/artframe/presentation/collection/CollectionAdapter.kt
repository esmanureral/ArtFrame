package com.esmanureral.artframe.presentation.collection

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.network.CollectionArtwork
import com.esmanureral.artframe.databinding.ItemCollectionBinding
import java.util.Locale

class CollectionAdapter(
    private var artworks: List<CollectionArtwork>
) : RecyclerView.Adapter<CollectionAdapter.ArtworkViewHolder>() {

    inner class ArtworkViewHolder(private val binding: ItemCollectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(artwork: CollectionArtwork) {
            loadArtworkImage(artwork.imageUrl)
            showFormattedPrice(artwork.price)
            applyOwnershipEffect(artwork.isOwned)
        }

        private fun loadArtworkImage(url: String?) {
            binding.imgArtworkWon.load(url) {
                crossfade(true)
                placeholder(android.R.color.darker_gray)
                error(android.R.color.holo_red_dark)
            }
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