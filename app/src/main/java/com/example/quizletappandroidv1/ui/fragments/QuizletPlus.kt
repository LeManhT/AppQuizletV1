package com.example.quizletappandroidv1.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentQuizletPlusBinding


class QuizletPlus : Fragment() {

    private var _binding: FragmentQuizletPlusBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentQuizletPlusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtTitlePlus = binding.txtTitlePlus

        // Get the complete text from the TextView
        val completeText = txtTitlePlus.text.toString()

        // Find the index of "Quizlet" in the complete text
        val quizletIndex = completeText.indexOf("Quizlet")

        val spannableString = SpannableString(completeText)

        spannableString.setSpan(
            ForegroundColorSpan(Color.WHITE),
            quizletIndex,
            quizletIndex + "Quizlet".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            ForegroundColorSpan(Color.YELLOW),
            quizletIndex + "Quizlet".length,
            completeText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        txtTitlePlus.text = spannableString

        binding.imgClose.setOnClickListener {
           findNavController().popBackStack()
        }

        binding.btnGoAchievement.setOnClickListener {
            findNavController().navigate(R.id.action_quizletPlus_to_achievements)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}