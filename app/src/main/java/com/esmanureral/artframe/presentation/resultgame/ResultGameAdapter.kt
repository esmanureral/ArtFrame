package com.esmanureral.artframe.presentation.resultgame

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.esmanureral.artframe.data.network.CorrectAnswer
import com.esmanureral.artframe.databinding.ItemResultGameBinding

class ResultGameAdapter(
    private val correctAnswers: List<CorrectAnswer>
) : RecyclerView.Adapter<ResultGameAdapter.WonArtworkViewHolder>() {

    inner class WonArtworkViewHolder(private val binding: ItemResultGameBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(answer: CorrectAnswer) {
            binding.imgArtworkWon.load(answer.imageUrl) {
                crossfade(true)
                placeholder(android.R.color.darker_gray)
                error(android.R.color.holo_red_dark)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WonArtworkViewHolder {
        val binding = ItemResultGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WonArtworkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WonArtworkViewHolder, position: Int) {
        holder.bind(correctAnswers[position])
    }

    override fun getItemCount(): Int = correctAnswers.size
}
