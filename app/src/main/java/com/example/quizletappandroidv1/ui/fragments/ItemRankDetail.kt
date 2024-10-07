package com.example.quizletappandroidv1.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentItemRankDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ItemRankDetail : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentItemRankDetailBinding
    private val args: ItemRankDetailArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemRankDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtBxh.text = "${resources.getString(R.string.rank)} ${args.rankItem.order}"
        binding.txtUsername.text = args.rankItem.userName
        binding.txtDob.text = args.rankItem.dateOfBirth
        binding.txtTotalPoint.text = args.rankItem.score.toString()

        binding.imgAvatar.setOnClickListener {
            findNavController().navigate(R.id.action_itemRankDetail_to_viewImage)
        }
    }

}