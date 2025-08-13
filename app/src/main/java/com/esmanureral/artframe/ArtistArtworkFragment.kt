package com.esmanureral.artframe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.esmanureral.artframe.databinding.FragmentArtistArtworkBinding
class ArtistArtworkFragment : Fragment() {

    private var _binding: FragmentArtistArtworkBinding? = null
    private val binding get() = _binding!!

    private val args: ArtistArtworkFragmentArgs by navArgs()
    private val viewModel: ArtWorkViewModel by viewModels()
    private lateinit var adapter: ArtistArtworkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentArtistArtworkBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvArtistTitle.text = args.artistName

        adapter = ArtistArtworkAdapter { artwork ->
            val action = ArtistArtworkFragmentDirections
                .actionArtistArtworkFragmentToDetailFragment(artwork.id)
            findNavController().navigate(action)
        }

        binding.rvArtworks.adapter = adapter
        binding.rvArtworks.layoutManager = LinearLayoutManager(requireContext())

        viewModel.artworks.observe(viewLifecycleOwner) { list ->
            list?.let { adapter.setData(it) }
        }

        viewModel.fetchArtworksByArtist(args.artistId)

        binding.rvArtworks.adapter = adapter
        binding.rvArtworks.layoutManager = LinearLayoutManager(requireContext())

        viewModel.artworks.observe(viewLifecycleOwner) { list ->
            list?.let { adapter.setData(it) }
        }

        viewModel.fetchArtworksByArtist(args.artistId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
