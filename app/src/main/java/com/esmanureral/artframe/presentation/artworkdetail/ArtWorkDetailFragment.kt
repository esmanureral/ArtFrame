package com.esmanureral.artframe.presentation.artworkdetail

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.transition.AutoTransition
import android.transition.TransitionManager
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
import com.esmanureral.artframe.setArtistDisplay
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
            bottomActionBar.ivFavorite.setOnClickListener {
                viewModel.artworkDetail.value?.let {
                    currentArtwork?.let { artwork ->
                        toggleFavorite(artwork)

                    }
                }
            }

            artistContainer.setOnClickListener {
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
            tvArtistDisplay.setArtistDisplay(artwork.artistTitle, artwork.artistDisplay)
            tvDate.text = artwork.dateDisplay
            tvMedium.text = artwork.thumbnail?.altText
            tvDimensions.text = artwork.dimension
            tvCreditLine.text = artwork.creditLine
            tvPlaceOrigin.text = artwork.placeOfOrigin

            if (artwork.description.isNullOrBlank()) {
                tvDescription.text = getString(R.string.no_description)
                tvDescription.visibility = View.VISIBLE
                ivDescriptionIcon.visibility = View.GONE
            } else {
                setupDescriptionToggle(artwork.description)
            }

            bottomActionBar.ivShare.setOnClickListener {
                currentArtwork?.imageId?.let { imageId ->
                    val imageUrl = getString(R.string.artwork_image_url, imageId)
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, imageUrl)
                    }
                    startActivity(Intent.createChooser(shareIntent, ""))
                }
            }
        }
    }

    private fun setupDescriptionToggle(description: String) {
        with(binding) {
            tvDescription.text = Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT)
            tvDescription.visibility = View.VISIBLE
            ivDescriptionIcon.visibility = View.VISIBLE
            var expanded = false
            descriptionContainer.setOnClickListener {
                expanded = !expanded
                if (expanded) expandDescription() else collapseDescription()
            }
        }
    }

    private fun expandDescription() = with(binding) {
        TransitionManager.beginDelayedTransition(descriptionContainer, AutoTransition())
        tvDescription.maxLines = Int.MAX_VALUE
        tvDescription.ellipsize = null
        ivDescriptionIcon.setImageResource(R.drawable.arrow_up)
    }

    private fun collapseDescription() = with(binding) {
        TransitionManager.beginDelayedTransition(descriptionContainer, AutoTransition())
        tvDescription.maxLines = 1
        tvDescription.ellipsize = TextUtils.TruncateAt.END
        ivDescriptionIcon.setImageResource(R.drawable.arrow_down)
    }

    private fun toggleFavorite(artwork: ArtworkDetailUI) {
        if (favoritesPrefs.isArtworkFavorite(artwork)) favoritesPrefs.removeArtworkFavorite(artwork)
        else favoritesPrefs.addArtworkFavorite(artwork)
        updateFavoriteIcon(artwork)
    }

    private fun updateFavoriteIcon(artwork: ArtworkDetailUI) {
        val res = if (favoritesPrefs.isArtworkFavorite(artwork)) R.drawable.favorite_24
        else R.drawable.favorite_border
        binding.bottomActionBar.ivFavorite.setImageResource(res)
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
