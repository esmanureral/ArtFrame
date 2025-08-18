package com.esmanureral.artframe.presentation.artistlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.databinding.FragmentArtistListBinding


class ArtistListFragment : Fragment() {
    private var _binding: FragmentArtistListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArtistListViewModel by viewModels()
    private lateinit var adapter: ArtistListAdapter
    private lateinit var favoritesPrefs: ArtWorkSharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        favoritesPrefs = ArtWorkSharedPreferences(requireContext())
        setupRecyclerView()
        observeViewModel()
        viewModel.fetchArtists()
    }

    private fun setupRecyclerView() {
        adapter = ArtistListAdapter(
            favoritesPrefs, onItemClick = { artist ->
                val action =
                    ArtistListFragmentDirections.actionArtistListFragmentToArtistArtworkFragment(
                        artist.id,
                        artist.title ?: ""
                    )
                findNavController().navigate(action)
            },
            isRemoveFavorite = false
        )

        with(binding) {
            rvArtists.adapter = adapter
            rvArtists.layoutManager = LinearLayoutManager(requireContext())
            rvArtists.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(rv, dx, dy)
                    checkForPagination(rv)

                }
            })
        }
    }

    private fun checkForPagination(rv: RecyclerView) {
        val layoutManager = rv.layoutManager as LinearLayoutManager
        val totalItemCount = layoutManager.itemCount
        val lastVisible = layoutManager.findLastVisibleItemPosition()
        if (lastVisible >= totalItemCount - 2) {
            viewModel.fetchArtists()
        }
    }

    private fun observeViewModel() {
        viewModel.artists.observe(viewLifecycleOwner) { newItems ->
            newItems?.let { adapter.addData(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}