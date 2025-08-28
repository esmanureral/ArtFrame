package com.esmanureral.artframe.presentation.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.esmanureral.artframe.R
import com.esmanureral.artframe.databinding.FragmentCollectionBinding
import com.esmanureral.artframe.presentation.game.QuizViewModel

class CollectionFragment : Fragment() {

    private var _binding: FragmentCollectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by viewModels()
    private lateinit var adapter: CollectionAdapter

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
        observePopularArtworks()
        setOnClickListener()
        viewModel.loadPopularArtworks()
    }

    private fun setupRecyclerView() {
        adapter = CollectionAdapter(emptyList())
        binding.recyclerView.adapter = adapter
    }

    private fun observePopularArtworks() = with(binding) {
        viewModel.popularArtworks.observe(viewLifecycleOwner) { artworks ->
            adapter.updateList(artworks)
            tvAllCollections.text = getString(R.string.artwork_count, artworks.size)
            tvYourCollections.text =
                getString(R.string.artwork_count, artworks.count() { it.isOwned })
        }
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
