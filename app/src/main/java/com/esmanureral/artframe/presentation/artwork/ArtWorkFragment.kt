package com.esmanureral.artframe.presentation.artwork

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.esmanureral.artframe.R
import com.esmanureral.artframe.databinding.FragmentArtworkListBinding

class ArtworkListFragment : Fragment() {

    private var _binding: FragmentArtworkListBinding? = null
    private val binding get() = _binding!!
    private var isDarkMode = false

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
        setupClickListeners()
        initThemeIcon()
    }

    private fun initThemeIcon() {
        val currentMode = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK
        isDarkMode = currentMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
        updateIcons()
    }

    private fun setupClickListeners() = with(binding) {
        ivVirtualIcon.setOnClickListener { navigateToVirtualGallery() }
        switchTheme.setOnClickListener { toggleTheme() }
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

    private fun navigateToVirtualGallery() {
        val action = ArtworkListFragmentDirections
            .actionArtworkListFragmentToVirtualArtGalleryFragment()
        findNavController().navigate(action)
    }

    private fun toggleTheme() {
        isDarkMode = !isDarkMode
        val newMode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(newMode)
        updateIcons()
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
        findNavController().navigate(
            ArtworkListFragmentDirections.actionArtworkListFragmentToDetailFragment(
                artworkId = artworkId
            )
        )
    }

    private fun updateIcons() {
        val iconRes = if (isDarkMode) R.drawable.light_theme
        else R.drawable.dark_theme
        binding.switchTheme.setImageResource(iconRes)
    }

    private fun observeViewModel() {
        viewModel.artworks.observe(viewLifecycleOwner) { newItems ->
            adapter.submitList(newItems)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
