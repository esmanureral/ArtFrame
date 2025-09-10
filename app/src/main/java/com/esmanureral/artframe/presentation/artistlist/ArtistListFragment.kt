package com.esmanureral.artframe.presentation.artistlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esmanureral.artframe.ArtFrameApplication
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.databinding.FragmentArtistListBinding
import com.esmanureral.artframe.presentation.artistlist.model.ArtistListUI
import com.esmanureral.artframe.presentation.deleteItem.DeleteItemType

class ArtistListFragment : Fragment() {
    private var _binding: FragmentArtistListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArtistListViewModel by lazy {
        val app = requireActivity().application as ArtFrameApplication
        ArtistListViewModel(app.apiService)
    }
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

    private fun setupRecyclerView() = with(binding) {
        adapter = createAdapter()
        rvArtists.adapter = adapter
        rvArtists.layoutManager = LinearLayoutManager(requireContext())
        rvArtists.addOnScrollListener(createScrollListener())
    }

    private fun createAdapter() =
        ArtistListAdapter(favoritesPrefs = favoritesPrefs, onItemClick = { artist ->
            navigateToArtistDetail(artist)
        }, isRemoveFavorite = false)

    private fun createScrollListener() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(rv, dx, dy)
            checkForPagination(rv = rv)
        }
    }

    private fun navigateToArtistDetail(artist: ArtistListUI) {
        val action = ArtistListFragmentDirections.actionArtistListFragmentToArtistArtworkFragment(
            artistId = artist.id,
            artistName = artist.title,
            birthDate = artist.birthDate ?: getString(R.string.year_unknown),
            deathDate = artist.deathDate ?: getString(R.string.year_unknown),
            itemType = DeleteItemType.ARTIST
        )
        findNavController().navigate(action)
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
            val currentList = adapter.currentList.toMutableList()
            currentList.addAll(newItems)
            adapter.submitList(currentList)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            setLoadingState(isLoading)
        }
    }

    private fun setLoadingState(isLoading: Boolean) = with(binding) {
        shimmerLayout.apply {
            if (isLoading) startShimmer() else stopShimmer()
            visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        rvArtists.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}