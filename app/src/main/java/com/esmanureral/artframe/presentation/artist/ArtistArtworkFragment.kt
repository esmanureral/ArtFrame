package com.esmanureral.artframe.presentation.artist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.esmanureral.artframe.PermissionHelper
import com.esmanureral.artframe.R
import com.esmanureral.artframe.databinding.FragmentArtistArtworkBinding

class ArtistArtworkFragment : Fragment() {

    private var _binding: FragmentArtistArtworkBinding? = null
    private val binding get() = _binding!!

    private val args: ArtistArtworkFragmentArgs by navArgs()
    private val viewModel: ArtistArtworkViewModel by viewModels()
    private lateinit var adapter: ArtistArtworkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentArtistArtworkBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvArtistTitle.text = args.artistName
        setupAdapter()
        observeViewModel()
        setupPermission()
        viewModel.fetchArtworksByArtist(args.artistId)
        setupBackButton()
    }

    private fun setupAdapter() {
        adapter = ArtistArtworkAdapter { artwork ->
            val action = ArtistArtworkFragmentDirections
                .actionArtistArtworkFragmentToDetailFragment(artwork.id)
            findNavController().navigate(action)
        }
        with(binding) {
            rvArtworks.adapter = adapter
            rvArtworks.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewModel.artworks.observe(viewLifecycleOwner) { list ->
            list?.let { adapter.setData(it) }
        }
    }

    private fun setupPermission() {
        PermissionHelper.requestNotificationPermission(this) { granted ->
            if (granted) println("Notification permission granted")
            else println("Notification permission denied")
        }
    }

    private fun setupBackButton() {
        binding.ivArrowLeft.setOnClickListener {
            findNavController().navigate(R.id.artistListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}