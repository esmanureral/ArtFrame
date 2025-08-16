package com.esmanureral.artframe.presentation.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.PermissionHelper
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.network.ArtworkDetail
import com.esmanureral.artframe.databinding.FavoritesFragmentBinding

class FavoritesFragment : Fragment() {
    private var _binding: FavoritesFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoritesPrefs: ArtWorkSharedPreferences
    private lateinit var adapter: FavoritesAdapter
    private var favoritesList = mutableListOf<ArtworkDetail>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FavoritesFragmentBinding.inflate(inflater, container, false)
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
            favoritesPrefs = ArtWorkSharedPreferences(requireContext())
            favoritesList = favoritesPrefs.loadFavorites().reversed().toMutableList()
            adapter = FavoritesAdapter(favoritesList) { artwork ->
                findNavController().navigate(
                    FavoritesFragmentDirections.actionFavoritesFragmentToDetailFragment(artwork.id)
                )
            }
            with(binding) {
                rvFavorites.adapter = adapter
                rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
            }
        }
    }
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
