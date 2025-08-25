package com.esmanureral.artframe

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.esmanureral.artframe.databinding.FragmentFullScreenImageBinding

class FullScreenImageFragment : Fragment() {

    private var _binding: FragmentFullScreenImageBinding? = null
    private val binding get() = _binding!!

    private val args: FullScreenImageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFullScreenImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivFullScreen.loadWithIndicator(
            url = args.imageUrl,
            progressIndicator = binding.progressIndicator,
            errorRes = R.drawable.error
        )

        binding.ivFullScreen.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

