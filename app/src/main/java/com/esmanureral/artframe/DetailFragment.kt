package com.esmanureral.artframe

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.esmanureral.artframe.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ArtWorkViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val artworkId = arguments?.getInt("artwork_id") ?: return

        viewModel.fetchArtworkDetail(artworkId)

        viewModel.artworkDetail.observe(viewLifecycleOwner) { detail ->
            detail?.let {
                with(binding){
                    tvTitle.text = it.title ?: "-"
                    tvArtist.text = it.artist_display ?: "-"
                    tvDate.text = it.date_display ?: "-"
                    tvMedium.text = it.medium_display ?: "-"
                    tvDimensions.text = it.dimensions ?: "-"
                    tvCredit.text = it.credit_line ?: "-"
                    tvDescription.text =
                        Html.fromHtml(it.description ?: "", Html.FROM_HTML_MODE_COMPACT)
                }
                val imageUrl = "https://www.artic.edu/iiif/2/${it.image_id}/full/843,/0/default.jpg"
                binding.ivArtwork.load(imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_background)
                    error(R.drawable.ic_launcher_foreground)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
