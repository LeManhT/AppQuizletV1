package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentWelcomeToLearnBinding

class FragmentWelcomeToLearn : Fragment() {
    private lateinit var binding: FragmentWelcomeToLearnBinding
    private val args: FragmentWelcomeToLearnArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeToLearnBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGoNow.setOnClickListener {
            val action = FragmentWelcomeToLearnDirections.actionFragmentWelcomeToLearnToReviewLearn(args.setId)
            findNavController().navigate(action)
        }
    }
}