package com.esmanureral.artframe.presentation.artistdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.network.Artwork
import com.esmanureral.artframe.databinding.ItemArtistArtworkBinding

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
                loadArtworkImage(ivArtworkImage, item.imageId)
                root.setOnClickListener { onItemClick(item) }
            }
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

    private fun loadArtworkImage(imageView: ImageView, imageId: String?) {
        val imageUrl = imageView.context.getString(R.string.artwork_image_url, imageId)
        imageView.load(imageUrl) {
            placeholder(R.drawable.black)
            error(R.drawable.error)
            crossfade(true)
        }
    }
}