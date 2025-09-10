package com.esmanureral.artframe.presentation.artworkdetail

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Html
import android.text.TextUtils
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.ImageLoader
import coil.request.ImageRequest
import com.esmanureral.artframe.ArtFrameApplication
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.R
import com.esmanureral.artframe.animateCollapseExpand
import com.esmanureral.artframe.databinding.FragmentDetailBinding
import com.esmanureral.artframe.loadWithIndicator
import com.esmanureral.artframe.orDefault
import com.esmanureral.artframe.presentation.artworkdetail.model.ArtworkDetailUI
import com.esmanureral.artframe.presentation.deleteItem.DeleteItemType
import com.esmanureral.artframe.setArtistDisplay
import com.esmanureral.artframe.showToast
import com.google.android.material.appbar.AppBarLayout
import java.io.File

class ArtWorkDetailFragment : Fragment(), ImageRequest.Listener {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArtWorkDetailViewModel by lazy {
        val app = requireActivity().application as ArtFrameApplication
        ArtWorkDetailViewModel(app.apiService)
    }
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
        viewModel.fetchArtworkDetail(id = artworkId)
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
            bottomActionBar.favoriteContainer.setOnClickListener {
                currentArtwork?.let { toggleFavorite(it) }
            }
            bottomActionBar.shareContainer.setOnClickListener {
                currentArtwork?.imageId?.let { imageId ->
                    val imageUrl = getString(R.string.artwork_image_url, imageId)
                    setImageAction(it, imageUrl) { bitmap ->
                        val uri = saveBitmapToCache(bitmap)
                        uri?.let { safeUri -> shareImageUri(safeUri) }
                    }
                }
            }

            bottomActionBar.downloadContainer.setOnClickListener {
                currentArtwork?.imageId?.let { imageId ->
                    val imageUrl = getString(R.string.artwork_image_url, imageId)
                    setImageAction(it, imageUrl) { bitmap ->
                        saveBitmapToGallery(bitmap)
                        requireContext().showToast(getString(R.string.image_saved))
                    }
                }
            }

            bottomActionBar.wallpaperContainer.setOnClickListener {
                currentArtwork?.imageId?.let { imageId ->
                    val imageUrl = getString(R.string.artwork_image_url, imageId)
                    setImageAction(it, imageUrl) { bitmap ->
                        val uri = saveBitmapToCache(bitmap)
                        uri?.let { safeUri -> setAsWallpaperWithChooser(safeUri) }
                    }
                }
            }

            artistContainer.setOnClickListener {
                currentArtwork?.let { navigateToArtistArtworks(it) }
            }

            ivArtwork.setOnClickListener {
                currentArtwork?.let {
                    val imageUrl = getString(R.string.artwork_image_url, it.imageId)
                    navigateToFullScreenImage(imageUrl = imageUrl)
                }
            }

            ivArrowLeft.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun updateUI(artwork: ArtworkDetailUI) {
        bindTextFields(artwork = artwork)
        loadArtworkImage(artwork = artwork)
        updateFavoriteIcon(artwork = artwork)
        binding.appBar.animateCollapseExpand(favoritesPrefs)
    }

    private fun loadArtworkImage(artwork: ArtworkDetailUI) = with(binding) {
        val imageUrl = getString(R.string.artwork_image_url, artwork.imageId)
        ivArtwork.loadWithIndicator(
            url = imageUrl,
            progressIndicator = progressIndicator,
            errorRes = R.drawable.error
        )
    }

    private fun bindTextFields(artwork: ArtworkDetailUI) {
        with(binding) {
            tvTitle.text = artwork.title.orDefault(getString(R.string.no_description))
            tvArtistDisplay.setArtistDisplay(
                artwork.artistTitle.orDefault(getString(R.string.no_artist)),
                artwork.artistDisplay.orDefault(getString(R.string.no_artist))
            )
            tvDate.text = artwork.dateStart.orDefault(getString(R.string.no_artist))
            tvMedium.text = artwork.thumbnail?.altText.orDefault(getString(R.string.no_medium))
            tvDimensions.text = artwork.dimension.orDefault(getString(R.string.no_dimension))
            tvCreditLine.text = artwork.creditLine.orDefault(getString(R.string.no_credit))
            tvPlaceOrigin.text = artwork.placeOfOrigin.orDefault(getString(R.string.no_place))

            if (artwork.description.isNullOrBlank()) {
                tvDescription.text = getString(R.string.no_description)
                tvDescription.visibility = View.VISIBLE
                ivDescriptionIcon.visibility = View.GONE
            } else {
                setupDescriptionToggle(artwork.description)
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
        if (favoritesPrefs.isArtworkFavorite(artwork))
            favoritesPrefs.removeArtworkById(artworkId = artwork.id)
        else
            favoritesPrefs.addArtworkFavorite(artwork)
        updateFavoriteIcon(artwork)
    }

    private fun updateFavoriteIcon(artwork: ArtworkDetailUI) {
        val res = if (favoritesPrefs.isArtworkFavorite(artwork)) R.drawable.favorite_24
        else R.drawable.favorite_border
        binding.bottomActionBar.ivFavorite.setImageResource(res)
    }

    private fun navigateToArtistArtworks(artwork: ArtworkDetailUI) {
        val action = ArtWorkDetailFragmentDirections
            .actionDetailFragmentToArtistArtworkFragment(
                artistId = artwork.artistId,
                artistName = artwork.artistTitle,
                birthDate = artwork.birthDate ?: "",
                deathDate = artwork.deathDate ?: "",
                itemType = DeleteItemType.ARTIST
            )
        findNavController().navigate(action)
    }

    private fun navigateToFullScreenImage(imageUrl: String) {
        val action = ArtWorkDetailFragmentDirections
            .actionDetailFragmentToFullScreenImageFragment(imageUrl)
        findNavController().navigate(action)
    }

    private fun loadBitmapFromUrl(imageUrl: String, callback: (Bitmap?) -> Unit) {
        val context = requireContext()
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false)
            .target { drawable ->
                callback((drawable as? BitmapDrawable)?.bitmap)
            }
            .listener(onError = { _, _ ->
                callback(null)
            })
            .build()
        loader.enqueue(request)
    }

    private fun saveBitmapToCache(bitmap: Bitmap): Uri? {
        val context = requireContext()
        val cachePath = File(context.cacheDir, "shared_images").apply { mkdirs() }
        val file = File(
            cachePath,
            "${getString(R.string.shared_image_prefix)}_${System.currentTimeMillis()}.png"
        )
        writeBitmapToFile(bitmap, file)

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    private fun saveBitmapToGallery(bitmap: Bitmap): Uri? {
        val filename = "${getString(R.string.art_frame_prefix)}_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/ArtFrame")
        }

        val resolver = requireContext().contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let { writeBitmapToUri(bitmap, it) }
        return uri
    }

    private fun writeBitmapToFile(bitmap: Bitmap, file: File) {
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    private fun writeBitmapToUri(bitmap: Bitmap, uri: Uri) {
        requireContext().contentResolver.openOutputStream(uri)?.use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    private fun shareImageUri(uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.shared_saved)))
    }

    private fun AppBarLayout.animateCollapseExpand(sharedPrefs: ArtWorkSharedPreferences) {
        if (!sharedPrefs.isAppBarAnimationSeen()) {
            animateCollapseExpand()
            sharedPrefs.setAppBarAnimationSeen()
        }
    }

    private fun setAsWallpaperWithChooser(uri: Uri) {
        val intent = Intent(Intent.ACTION_ATTACH_DATA).apply {
            setDataAndType(uri, "image/*")
            putExtra("mimeType", "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.wallpaper)))
    }

    private fun setImageAction(
        containerView: View,
        imageUrl: String,
        onBitmapReady: (Bitmap) -> Unit
    ) {
        val bottomBar = binding.bottomActionBar
        containerView.isEnabled = false
        bottomBar.downloadProgressBar.visibility = View.VISIBLE

        loadBitmapFromUrl(imageUrl) { bitmap ->
            bitmap?.let(onBitmapReady)
            bottomBar.downloadProgressBar.visibility = View.GONE
            containerView.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}