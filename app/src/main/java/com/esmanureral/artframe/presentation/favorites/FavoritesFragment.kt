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
import com.google.android.material.tabs.TabLayout

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
        }
    }

    private fun setupTabClicks() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
                        binding.rvFavorites.adapter = adapter
                    }

                    1 -> {
                        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvFavorites.adapter = artistAdapter
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
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
