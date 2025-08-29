package com.esmanureral.artframe.presentation.virtual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.esmanureral.artframe.R
import com.esmanureral.artframe.databinding.FragmentVirtualArtGalleryBinding

class VirtualArtGalleryFragment : Fragment() {

    private var _binding: FragmentVirtualArtGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVirtualArtGalleryBinding.inflate(inflater, container, false)

        with(binding) {
            webviewGallery.apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = WebViewClient()
                loadUrl(getString(R.string.virtual_image_url))
            }
            btnClose.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.webviewGallery.destroy()
        _binding = null
    }
}