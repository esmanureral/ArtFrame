package com.esmanureral.artframe.presentation.artwork

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esmanureral.artframe.R
import com.esmanureral.artframe.databinding.FragmentArtworkListBinding

class ArtworkListFragment : Fragment() {

    private var _binding: FragmentArtworkListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ArtWorkViewModel by viewModels()
    private lateinit var adapter: ArtworkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtworkListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = ArtworkAdapter { artwork ->
            navigateToDetail(artwork.id)
        }

        with(binding) {
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            recyclerView.adapter = adapter
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(rv, dx, dy)
                    checkForPagination(rv)
                }
            })
        }
    }

    private fun checkForPagination(rv: RecyclerView) {
        val layoutManager = rv.layoutManager as GridLayoutManager
        val totalItemCount = layoutManager.itemCount
        val lastVisible = layoutManager.findLastVisibleItemPosition()
        if (lastVisible >= totalItemCount - 2) {
            viewModel.fetchArtworks()
        }
    }

    private fun navigateToDetail(artworkId: Int) {
        val navController = requireActivity().findNavController(R.id.nav_host_fragment)
        navController.navigate(
            ArtworkListFragmentDirections.actionArtworkListFragmentToDetailFragment(artworkId)
        )
    }

    private fun observeViewModel() {
        viewModel.artworks.observe(viewLifecycleOwner) { newItems ->
            adapter.submitList(newItems.toList())

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
