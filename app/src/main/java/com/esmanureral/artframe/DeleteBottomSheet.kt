package com.esmanureral.artframe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.esmanureral.artframe.databinding.BottomSheetDeleteBinding
class DeleteBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetDeleteBinding? = null
    private val binding get() = _binding!!

    private var listener: DeleteListener? = null

    interface DeleteListener {
        fun onDeleteItem()
        fun onDeleteAll()
    }

    fun setListener(listener: DeleteListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetDeleteBinding.inflate(inflater, container, false)

        binding.tvDeleteItem.setOnClickListener {
            listener?.onDeleteItem()
            dismiss()
        }

        binding.tvDeleteAll.setOnClickListener {
            listener?.onDeleteAll()
            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}