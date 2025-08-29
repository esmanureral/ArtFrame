package com.esmanureral.artframe.presentation.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.databinding.FavoritesFragmentBinding
import com.esmanureral.artframe.presentation.artistlist.ArtistListAdapter
import com.esmanureral.artframe.presentation.artistlist.model.ArtistListUI
import com.esmanureral.artframe.presentation.artworkdetail.model.ArtworkDetailUI
import com.esmanureral.artframe.presentation.deleteItem.DeleteItemType
import com.google.android.material.tabs.TabLayout

class FavoritesFragment : Fragment() {
    private var _binding: FavoritesFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var favoritesPrefs: ArtWorkSharedPreferences

    private lateinit var adapter: FavoritesAdapter
    private lateinit var artistAdapter: ArtistListAdapter
    private var favoriteArtwork = mutableListOf<ArtworkDetailUI>()
    private var favoriteArtists = mutableListOf<ArtistListUI>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavoritesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesPrefs = ArtWorkSharedPreferences(requireContext())
        favoriteArtwork = favoritesPrefs.loadArtworkFavorites().reversed().toMutableList()
        favoriteArtists = favoritesPrefs.loadArtistFavorites().reversed().toMutableList()
        setupAdapters()
        setupTabClicks()

        with(binding) {
            rvFavorites.adapter = adapter
            rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
        }

        checkEmptyState()
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
                checkEmptyState()
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
            },
            onFavoritesChanged = {
                checkEmptyState()
            },
            parentFragment = this
        )

        artistAdapter = ArtistListAdapter(
            favoritesPrefs = favoritesPrefs,
            onItemClick = { artist ->
                val action =
                    FavoritesFragmentDirections.actionFavoritesFragmentToArtistArtworkFragment(
                        artistId = artist.id,
                        artistName = artist.title,
                        birthDate = artist.birthDate ?: "",
                        deathDate = artist.deathDate ?: "",
                        itemType = DeleteItemType.ARTIST
                    )
                findNavController().navigate(action)
            },
            isRemoveFavorite = true,
            parentFragment = this
        )
        artistAdapter.submitList(favoriteArtists)
    }

    private fun checkEmptyState() {
        val currentTab = binding.tabLayout.selectedTabPosition
        with(binding) {
            when (currentTab) {
                0 -> {
                    if (favoriteArtwork.isEmpty()) {
                        rvFavorites.visibility = View.GONE
                        tvEmptyMessage.visibility = View.VISIBLE
                        tvEmptyMessage.text = getString(R.string.no_favorites)
                    } else {
                        rvFavorites.visibility = View.VISIBLE
                        tvEmptyMessage.visibility = View.GONE
                    }
                }

                1 -> {
                    if (favoriteArtists.isEmpty()) {
                        rvFavorites.visibility = View.GONE
                        tvEmptyMessage.visibility = View.VISIBLE
                        tvEmptyMessage.text = getString(R.string.no_favorites)
                    } else {
                        rvFavorites.visibility = View.VISIBLE
                        tvEmptyMessage.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
