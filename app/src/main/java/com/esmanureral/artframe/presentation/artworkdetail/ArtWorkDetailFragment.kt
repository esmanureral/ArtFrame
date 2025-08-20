package com.esmanureral.artframe.presentation.artworkdetail

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.R
import com.esmanureral.artframe.animateCollapseExpand
import com.esmanureral.artframe.databinding.FragmentDetailBinding
import com.esmanureral.artframe.presentation.artworkdetail.model.ArtworkDetailUI
import com.google.android.material.appbar.AppBarLayout

class ArtWorkDetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArtWorkDetailViewModel by viewModels()
    private var currentArtwork: ArtworkDetailUI? = null
    private lateinit var favoritesPrefs: ArtWorkSharedPreferences
    private val args: ArtWorkDetailFragmentArgs by navArgs()

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
        val artworkId = args.artworkId
        viewModel.fetchArtworkDetail(artworkId)
        setupClickListeners()
        observeArtworkDetail()
    }

    private fun observeArtworkDetail() {
        viewModel.artworkDetail.observe(viewLifecycleOwner) { detail ->
            detail?.let {
                currentArtwork = it
                updateUI(it)
            }
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            ivFavorite.setOnClickListener {
                viewModel.artworkDetail.value?.let {
                    currentArtwork?.let { artwork ->
                        toggleFavorite(artwork)

                    }
                }
            }

            ivArtistIcon.setOnClickListener {
                currentArtwork?.let { artwork -> navigateToArtistArtworks(artwork) }
            }

            ivArtwork.setOnClickListener {
                currentArtwork?.let { artwork ->
                    val imageUrl =
                        root.context.getString(R.string.artwork_image_url, artwork.imageId)
                    navigateToFullScreenImage(imageUrl)
                }
            }
            ivArrowLeft.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun updateUI(artwork: ArtworkDetailUI) {
        bindTextFields(artwork)
        loadArtworkImage(artwork)
        updateFavoriteIcon(artwork)
        binding.appBar.animateCollapseExpand(favoritesPrefs)
    }

    private fun loadArtworkImage(artwork: ArtworkDetailUI) {
        val imageUrl =
            "https://www.artic.edu/iiif/2/${artwork.imageId}/full/!1280,720/0/default.jpg"
        binding.ivArtwork.load(imageUrl) {
            crossfade(true)
            placeholder(R.drawable.black)
            error(R.drawable.error)
        }
    }

    private fun bindTextFields(artwork: ArtworkDetailUI) {
        with(binding) {
            tvTitle.text = artwork.title
            tvArtistDisplay.text = artwork.artistDisplay
            tvDate.text = artwork.dateDisplay
            tvMedium.text = artwork.thumbnail?.altText
            tvDimensions.text = artwork.dimension
            tvCreditLine.text = artwork.creditLine
            tvPlaceOrigin.text = artwork.placeOfOrigin
            tvDescription.text = Html.fromHtml(
                artwork.description,
                Html.FROM_HTML_MODE_COMPACT
            )
        }
    }

    private fun toggleFavorite(artwork: ArtworkDetailUI) {
        if (favoritesPrefs.isArtworkFavorite(artwork)) favoritesPrefs.removeArtworkFavorite(artwork)
        else favoritesPrefs.addArtworkFavorite(artwork)
        updateFavoriteIcon(artwork)
    }

    private fun updateFavoriteIcon(artwork: ArtworkDetailUI) {
        val res = if (favoritesPrefs.isArtworkFavorite(artwork)) R.drawable.favorite_24
        else R.drawable.favorite_border
        binding.ivFavorite.setImageResource(res)
    }

    private fun navigateToArtistArtworks(artwork: ArtworkDetailUI) {
        artwork.artistId.let { id ->
            val action = ArtWorkDetailFragmentDirections
                .actionDetailFragmentToArtistArtworkFragment(
                    artistId = id,
                    artistName = artwork.artistTitle,
                    birthDate = artwork.birthDate ?: "",
                    deathDate = artwork.deathDate ?: ""
                )
            findNavController().navigate(action)
        }
    }

    private fun navigateToFullScreenImage(imageUrl: String) {
        val action = ArtWorkDetailFragmentDirections
            .actionDetailFragmentToFullScreenImageFragment(imageUrl)
        findNavController().navigate(action)
    }

    private fun AppBarLayout.animateCollapseExpand(sharedPrefs: ArtWorkSharedPreferences) {
        if (!sharedPrefs.isAppBarAnimationSeen()) {
            animateCollapseExpand()
            sharedPrefs.setAppBarAnimationSeen()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
