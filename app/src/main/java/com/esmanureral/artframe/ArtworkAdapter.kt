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

    inner class ArtworkViewHolder(private val binding: ItemArtworkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Artwork) {
            binding.title.text = item.title
            val imageUrl = "https://www.artic.edu/iiif/2/${item.imageId}/full/843,/0/default.jpg"
            binding.image.load(imageUrl) {
                crossfade(true)
            }
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtworkViewHolder {
        val binding = ItemArtworkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtworkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtworkViewHolder, position: Int) {
        holder.bind(artworks[position])
    }

    override fun getItemCount() = artworks.size

    fun updateData(newArtworks: List<Artwork>) {
        artworks.clear()
        artworks.addAll(newArtworks)
        notifyDataSetChanged()
    }
}

