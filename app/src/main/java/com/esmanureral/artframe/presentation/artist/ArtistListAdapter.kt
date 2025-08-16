package com.esmanureral.artframe.presentation.artist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.network.Artists
import com.esmanureral.artframe.databinding.ItemArtistBinding

class ArtistListAdapter(
    private val onItemClick: (Artists) -> Unit
) : RecyclerView.Adapter<ArtistListAdapter.ArtistViewHolder>() {
    private val artistItems = mutableListOf<Artists>()

    inner class ArtistViewHolder(private val binding: ItemArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(artists: Artists) {
            val context = binding.root.context
            with(binding) {
                tvArtistName.text = artists.title ?: context.getString(R.string.artist_unknown)
                val birth = artists.birthDate ?: context.getString(R.string.year_unknown)
                val death = artists.deathDate ?: context.getString(R.string.year_unknown)
                tvYears.text = context.getString(R.string.artist_years, birth, death)

                root.setOnClickListener {
                    onItemClick(artists)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val binding = ItemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(artistItems[position])
    }

    override fun getItemCount(): Int = artistItems.size

    fun addData(newArtists: List<Artists>) {
        val startPos = artistItems.size
        artistItems.addAll(newArtists)
        notifyItemRangeInserted(startPos, newArtists.size)
    }
}