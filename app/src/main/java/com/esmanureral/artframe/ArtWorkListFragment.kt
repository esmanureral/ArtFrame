package com.esmanureral.artframe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        adapter = ArtworkAdapter(mutableListOf()) { artwork ->
            val action = ArtworkListFragmentDirections
                .actionArtworkListFragmentToDetailFragment(artwork.id)
            findNavController().navigate(action)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                val layoutManager = rv.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                if (lastVisible >= totalItemCount - 2) {
                    viewModel.fetchArtworks()
                }
            }
        })

        viewModel.artworks.observe(viewLifecycleOwner) { newItems ->
            adapter.addData(newItems)
        }
        viewModel.fetchArtworks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
