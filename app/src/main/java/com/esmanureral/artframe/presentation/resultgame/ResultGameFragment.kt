package com.esmanureral.artframe.presentation.resultgame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.esmanureral.artframe.data.local.ArtWorkSharedPreferences
import com.esmanureral.artframe.databinding.FragmentResultGameBinding
import com.esmanureral.artframe.presentation.wonartworks.ResultGameAdapter

class ResultGameFragment : Fragment() {

    private var _binding: FragmentResultGameBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: ArtWorkSharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = ArtWorkSharedPreferences(requireContext())
        val correctAnswers = prefs.loadCorrectAnswers()
        val adapter = ResultGameAdapter(correctAnswers)
        binding.recyclerView.apply {
            this.adapter = adapter
        }
        setOnClickListener()
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