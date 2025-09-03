package com.esmanureral.artframe.presentation.artwork

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.esmanureral.artframe.R
import com.esmanureral.artframe.databinding.ItemArtworkBinding
import com.esmanureral.artframe.loadWithIndicator
import com.esmanureral.artframe.presentation.artwork.model.ArtworkUI

class ArtworkAdapter(
    private val onItemClick: (ArtworkUI) -> Unit
) : ListAdapter<ArtworkUI, ArtworkAdapter.ArtworkViewHolder>(ArtworkDiffCallback()) {

    class ArtworkViewHolder(
        private val binding: ItemArtworkBinding,
        private val onItemClick: (ArtworkUI) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ArtworkUI) = with(binding) {
            tvTitle.text = item.title
            loadArtwork(imageId = item.imageId)
            root.setOnClickListener { onItemClick(item) }
        }

        private fun ItemArtworkBinding.loadArtwork(imageId: String) {
            val url = root.context.getString(R.string.artwork_image_url, imageId)
            ivArtwork.loadWithIndicator(
                url = url,
                progressIndicator = progressIndicator,
                errorRes = R.drawable.error
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtworkViewHolder {
        val binding = ItemArtworkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtworkViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ArtworkViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ArtworkDiffCallback : DiffUtil.ItemCallback<ArtworkUI>() {
        override fun areItemsTheSame(oldItem: ArtworkUI, newItem: ArtworkUI) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ArtworkUI, newItem: ArtworkUI) = oldItem == newItem
    }
}
