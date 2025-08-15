package com.esmanureral.artframe

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.esmanureral.artframe.databinding.FragmentDetailBinding

class ArtWorkDetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArtWorkDetailViewModel by viewModels()
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
        PermissionHelper.requestNotificationPermission(this) { granted ->
            if (granted) {
                println(" Notification permission granted")
            } else {
                println(" Notification permission denied")
            }
        }
        favoritesPrefs = ArtWorkSharedPreferences(requireContext())

        val artworkId = arguments?.getInt("artwork_id") ?: return

        viewModel.fetchArtworkDetail(artworkId)

        viewModel.artworkDetail.observe(viewLifecycleOwner) { detail ->
            detail?.let {
                currentArtwork = it
                with(binding) {
                    tvTitle.text =
                        getString(R.string.variable, it.title ?: "")
                    chipArtist.text = getString(R.string.variable, it.artistTitle ?: "")
                    tvDate.text = getString(R.string.variable, it.dateDisplay ?: "")
                    tvMedium.text = getString(R.string.variable, it.thumbnail?.altText ?: "")
                    tvDescription.text = Html.fromHtml(
                        getString(R.string.artwork_description, it.description ?: ""),
                        Html.FROM_HTML_MODE_COMPACT
                    )
                    tvDimensions.text = getString(R.string.variable, it.dimensions ?: "")
                    tvCreditLine.text = getString(R.string.variable, it.creditLine ?: "")
                    val imageUrl =
                        "https://www.artic.edu/iiif/2/${it.imageId}/full/!1280,720/0/default.jpg"
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
                    chipArtist.setOnClickListener {
                        currentArtwork?.artistId?.let { id ->
                            val action = ArtWorkDetailFragmentDirections
                                .actionDetailFragmentToArtistArtworkFragment(
                                    artistId = id,
                                    artistName = currentArtwork?.artistTitle ?: "-"
                                )
                            findNavController().navigate(action)
                        }
                    }
                    ivArtwork.setOnClickListener {
                        val action =
                            ArtWorkDetailFragmentDirections.actionDetailFragmentToFullScreenImageFragment(
                                imageUrl
                            )
                        findNavController().navigate(action)
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
