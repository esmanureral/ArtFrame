package com.esmanureral.artframe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.esmanureral.artframe.databinding.ItemArtistArtworkBinding

class ArtistArtworkAdapter(
    private val artworks: MutableList<Artwork> = mutableListOf(),
    private val onItemClick: (Artwork) -> Unit = {}
) : RecyclerView.Adapter<ArtistArtworkAdapter.ArtworkViewHolder>() {

    inner class ArtworkViewHolder(
        private val binding: ItemArtistArtworkBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Artwork) {
            binding.tvArtworkTitle.text = item.title
            val imageUrl = "https://www.artic.edu/iiif/2/${item.imageId}/full/843,/0/default.jpg"
            binding.ivArtworkImage.load(imageUrl) { crossfade(true) }
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ArtworkViewHolder(
            ItemArtistArtworkBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ArtworkViewHolder, position: Int) =
        holder.bind(artworks[position])

    override fun getItemCount() = artworks.size

    fun setData(newArtworks: List<Artwork>) {
        artworks.clear()
        artworks.addAll(newArtworks)
        notifyDataSetChanged()
    }
}
