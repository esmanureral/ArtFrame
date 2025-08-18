package com.esmanureral.artframe.presentation.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.PermissionHelper
import com.esmanureral.artframe.data.network.Artists
import com.esmanureral.artframe.data.network.ArtworkDetail
import com.esmanureral.artframe.databinding.FavoritesFragmentBinding
import com.esmanureral.artframe.presentation.artist.ArtistListAdapter

class FavoritesFragment : Fragment() {
    private var _binding: FavoritesFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var favoritesPrefs: ArtWorkSharedPreferences

    private lateinit var adapter: FavoritesAdapter
    private lateinit var artistAdapter: ArtistListAdapter
    private var favoriteArtwork = mutableListOf<ArtworkDetail>()
    private var favoriteArtists = mutableListOf<Artists>()

    private var isSelected = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavoritesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPermission()

        favoritesPrefs = ArtWorkSharedPreferences(requireContext())
        favoriteArtwork = favoritesPrefs.loadArtworkFavorites().reversed().toMutableList()
        favoriteArtists = favoritesPrefs.loadArtistFavorites().reversed().toMutableList()
        setupAdapters()
        setupTabClicks()
        with(binding) {
            rvFavorites.adapter = adapter
            rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
            viewArtworkIndicator.visibility = View.VISIBLE

        }
    }

    private fun setupTabClicks() {
        with(binding) {
            tvArtworkHeader.setOnClickListener {
                isSelected = true
                showCurrentList()
            }
            tvArtistHeader.setOnClickListener {
                isSelected = false
                showCurrentList()
            }
        }
    }

    private fun showCurrentList() {
        with(binding) {
            if (isSelected) {
                rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
                rvFavorites.adapter = adapter

                viewArtworkIndicator.visibility = View.VISIBLE
                viewArtistIndicator.visibility = View.GONE

            } else {
                rvFavorites.layoutManager = LinearLayoutManager(requireContext())
                rvFavorites.adapter = artistAdapter

                viewArtworkIndicator.visibility = View.GONE
                viewArtistIndicator.visibility = View.VISIBLE
            }
        }
    }


    private fun setupAdapters() {
        adapter = FavoritesAdapter(
            favoritesPrefs = favoritesPrefs,
            favorites = favoriteArtwork,
            onItemClick = { artwork ->
                findNavController().navigate(
                    FavoritesFragmentDirections.actionFavoritesFragmentToDetailFragment(artwork.id)
                )
            }
        )

        artistAdapter = ArtistListAdapter(
            favoritesPrefs = favoritesPrefs,
            onItemClick = { artist ->
                val action =
                    FavoritesFragmentDirections.actionFavoritesFragmentToArtistArtworkFragment(
                        artist.id,
                        artist.title ?: ""
                    )
                findNavController().navigate(action)
            },
            isRemoveFavorite = true
        )
        artistAdapter.addData(favoriteArtists)
    }

    private fun setupPermission() {
        PermissionHelper.requestNotificationPermission(this) { granted ->
            println("Notification permission ${if (granted) "granted" else "denied"}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
