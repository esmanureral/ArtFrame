package com.esmanureral.artframe.presentation.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.esmanureral.artframe.R
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.data.network.CollectionArtwork
import com.esmanureral.artframe.databinding.FragmentCollectionBinding
import java.text.NumberFormat
import java.util.Locale

class CollectionFragment : Fragment() {

    private var _binding: FragmentCollectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CollectionAdapter
    private lateinit var pref: ArtWorkSharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setOnClickListener()
        getCollections()
    }

    private fun getCollections() {
        binding.progressBarCollections.isVisible = true
        pref = ArtWorkSharedPreferences(requireContext())
        val collections = pref.loadPopularArtworks().sortedByDescending { it.isOwned }
        loadData(collections)
        binding.progressBarCollections.isVisible = false
    }

    private fun loadData(artworks: List<CollectionArtwork>) = with(binding) {
        adapter.updateList(artworks)
        tvAllCollections.text = getString(R.string.artwork_count, artworks.size)
        tvYourCollections.text =
            getString(R.string.artwork_count, artworks.count { it.isOwned })

        val ownedValue = artworks.filter { it.isOwned }.sumOf { it.price }
        val formattedValue = NumberFormat.getNumberInstance(Locale.US).format(ownedValue.toLong())
        tvCollectionsValue.text = "$$formattedValue"
    }

    private fun setupRecyclerView() {
        adapter = CollectionAdapter(
            emptyList(),
            onClick = { id ->
                val action =
                    CollectionFragmentDirections.actionResultGameFragmentToDetailFragment(id)
                findNavController().navigate(action)
            },
            onItemsNotFound = { notFoundItemList ->
                val current = pref.loadPopularArtworks().toMutableList()
                current.removeAll { it.artworkId in notFoundItemList }
                pref.savePopularArtworks(current)
                getCollections()
            }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun setOnClickListener() = with(binding) {
        ivArrowLeft.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
