package com.esmanureral.artframe.presentation.artistdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.network.Artwork
import com.esmanureral.artframe.databinding.ItemArtistArtworkBinding
import com.esmanureral.artframe.loadWithIndicator

class ArtistDetailAdapter(
    private val artworks: MutableList<Artwork> = mutableListOf(),
    private val onItemClick: (Artwork) -> Unit = {}
) : RecyclerView.Adapter<ArtistDetailAdapter.ArtworkViewHolder>() {

    inner class ArtworkViewHolder(
        private val binding: ItemArtistArtworkBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Artwork) {
            with(binding) {
                tvArtworkTitle.text = item.title
                loadArtworkImage(binding, item.imageId)
                root.setOnClickListener { onItemClick(item) }
            }
        }

        private fun loadArtworkImage(binding: ItemArtistArtworkBinding, imageId: String?) {
            val imageUrl = binding.root.context.getString(R.string.artwork_image_url, imageId)
            binding.ivArtworkImage.loadWithIndicator(
                url = imageUrl,
                progressIndicator = binding.progressIndicator,
                errorRes = R.drawable.error
            )
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