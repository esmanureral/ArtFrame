package com.esmanureral.artframe.presentation.artistdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.data.network.Artwork
import com.esmanureral.artframe.databinding.FragmentArtistArtworkBinding
import com.esmanureral.artframe.presentation.artistlist.model.ArtistListUI
import com.esmanureral.artframe.presentation.deleteItem.DeleteItemType

class ArtistDetailFragment : Fragment() {

    private var _binding: FragmentArtistArtworkBinding? = null
    private val binding get() = _binding!!
    private val args: ArtistDetailFragmentArgs by navArgs()
    private val viewModel: ArtistDetailViewModel by viewModels()
    private lateinit var adapter: ArtistDetailAdapter
    private lateinit var favoritesPrefs: ArtWorkSharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentArtistArtworkBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        favoritesPrefs = ArtWorkSharedPreferences(requireContext())
        with(binding) {
            tvArtistTitle.text = args.artistName
            tvYears.text = getString(
                R.string.artist_years,
                args.birthDate.ifEmpty { "?" },
                args.deathDate.ifEmpty { "?" }
            )
        }
        setupAdapter()
        setOnClickListener()
        observeViewModel()
        viewModel.fetchArtworksByArtist(args.artistId)
        setupFavoriteIcon()
    }

    private fun deleteSingleItem() {
        when (args.itemType) {
            DeleteItemType.ARTIST -> {
                favoritesPrefs.removeArtistById(artistId = args.artistId)

            }

            DeleteItemType.ARTWORK -> {
                favoritesPrefs.removeArtworkById(artworkId = args.artistId)
            }
        }
    }
    private fun deleteAllItems() {
        when (args.itemType) {
            DeleteItemType.ARTIST -> {
                favoritesPrefs.removeAllArtists()

            }

            DeleteItemType.ARTWORK -> {
                favoritesPrefs.removeAllArtworks()
            }
        }
    }

    private fun setupAdapter() {
        adapter = ArtistDetailAdapter { artwork ->
            val action = ArtistDetailFragmentDirections
                .actionArtistArtworkFragmentToDetailFragment(artwork.id)
            findNavController().navigate(action)
        }
        binding.rvArtworks.adapter = adapter
    }

    private fun setOnClickListener() = with(binding) {
        ivArrowLeft.setOnClickListener {
            findNavController().popBackStack()
        }

        ivFavorite.setOnClickListener {
            toggleFavoriteArtist()
        }
    }

    private fun observeViewModel() {
        viewModel.artworks.observe(viewLifecycleOwner) { list ->
            updateArtworksUI(list)
        }
    }

    private fun setupFavoriteIcon() {
        val artist = createArtist
        updateFavoriteIcon(artist)
    }

    private fun updateFavoriteIcon(artist: ArtistListUI) {
        val iconRes = if (favoritesPrefs.isArtistFavorite(artist)) {
            R.drawable.favorite_24
        } else {
            R.drawable.favorite_border
        }
        binding.ivFavorite.setImageResource(iconRes)
    }

    private fun updateArtworksUI(artworks: List<Artwork>) = with(binding) {
        if (artworks.isEmpty()) {
            tvNoArtworks.text = getString(R.string.artist_details_not_found)
            tvNoArtworks.visibility = View.VISIBLE
            rvArtworks.visibility = View.GONE
        } else {
            tvNoArtworks.visibility = View.GONE
            rvArtworks.visibility = View.VISIBLE
            adapter.setData(artworks)
        }
    }

    private val createArtist: ArtistListUI by lazy {
        ArtistListUI(
            id = args.artistId,
            title = args.artistName,
            birthDate = args.birthDate,
            deathDate = args.deathDate
        )
    }

    private fun toggleFavoriteArtist() {
        createArtist.apply {
            if (favoritesPrefs.isArtistFavorite(this)) {
                favoritesPrefs.removeArtistById(artistId = args.artistId)
            } else {
                favoritesPrefs.addArtistFavorite(this)
            }
            updateFavoriteIcon(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}