package com.esmanureral.artframe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.esmanureral.artframe.databinding.ItemArtworkBinding

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
                tvclassificationTitle.text = item.classificationTitle
                val imageUrl =
                    "https://www.artic.edu/iiif/2/${item.imageId}/full/!1280,720/0/default.jpg"
                ivArtwork.load(imageUrl) {
                    crossfade(true)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtworkViewHolder {
        val binding = ItemArtworkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

