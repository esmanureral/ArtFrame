package com.esmanureral.artframe

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.esmanureral.artframe.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArtWorkViewModel by viewModels()
    private var currentArtwork: ArtworkDetail? = null
    private lateinit var favoritesPrefs: ArtWorkSharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoritesPrefs = ArtWorkSharedPreferences(requireContext())

        val artworkId = arguments?.getInt("artwork_id") ?: return

        viewModel.fetchArtworkDetail(artworkId)

        viewModel.artworkDetail.observe(viewLifecycleOwner) { detail ->
            detail?.let {
                currentArtwork = it
                with(binding) {
                    tvTitle.text = it.title ?: "-"
                    tvArtist.text = it.artistDisplay ?: "-"
                    tvDate.text = it.dateDisplay ?: "-"
                    tvMedium.text = it.mediumDisplay ?: "-"
                    tvDescription.text =
                        Html.fromHtml(it.description ?: "", Html.FROM_HTML_MODE_COMPACT)

                    val imageUrl =
                        "https://www.artic.edu/iiif/2/${it.imageId}/full/843,/0/default.jpg"
                    ivArtwork.load(imageUrl) {
                        crossfade(true)
                        placeholder(R.drawable.ic_launcher_background)
                        error(R.drawable.ic_launcher_foreground)
                    }
                    updateFavoriteIcon(it)
                    ivFavorite.setOnClickListener {
                        currentArtwork?.let { artwork ->
                            if (favoritesPrefs.isFavorite(artwork)) {
                                favoritesPrefs.removeFavorite(artwork)
                            } else {
                                favoritesPrefs.addFavorite(artwork)
                            }
                            updateFavoriteIcon(artwork)
                        }
                    }
                }
            }
        }
    }

    private fun updateFavoriteIcon(artwork: ArtworkDetail) {
        val isFav = favoritesPrefs.isFavorite(artwork)
        if (isFav) {
            binding.ivFavorite.setImageResource(R.drawable.favorite_24)
        } else {
            binding.ivFavorite.setImageResource(R.drawable.favorite_border)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
