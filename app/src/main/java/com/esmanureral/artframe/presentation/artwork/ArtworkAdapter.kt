package com.esmanureral.artframe.presentation.artwork

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.network.Artwork
import com.esmanureral.artframe.databinding.ItemArtworkBinding
import com.esmanureral.artframe.loadWithShimmer

class ArtworkAdapter(
    private val artworks: MutableList<Artwork>,
    private val onItemClick: (Artwork) -> Unit
) : RecyclerView.Adapter<ArtworkAdapter.ArtworkViewHolder>() {

    class ArtworkViewHolder(
        private val binding: ItemArtworkBinding,
        private val onItemClick: (Artwork) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Artwork) {
            with(binding) {
                root.setOnClickListener { onItemClick(item) }
                tvTitle.text = item.title

                val imageUrl =
                    "https://www.artic.edu/iiif/2/${item.imageId}/full/!1280,720/0/default.jpg"
                ivArtwork.loadWithShimmer(
                    url = imageUrl,
                    shimmerLayout = shimmerLayout,
                    errorRes = R.drawable.error
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtworkViewHolder {
        val binding =
            ItemArtworkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtworkViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ArtworkViewHolder, position: Int) {
        holder.bind(artworks[position])
    }

    override fun getItemCount() = artworks.size

    fun addData(newArtworks: List<Artwork>) {
        val startPos = artworks.size
        artworks.addAll(newArtworks)
        notifyItemRangeInserted(startPos, newArtworks.size)
    }
}

