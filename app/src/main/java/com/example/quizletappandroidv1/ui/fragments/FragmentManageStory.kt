package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.quizletappandroidv1.databinding.FragmentManageStoryBinding
import com.example.quizletappandroidv1.viewmodel.favourite.FavouriteNewWordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentManageStory : Fragment() {
    private lateinit var binding: FragmentManageStoryBinding
    private val newWordViewModel: FavouriteNewWordViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageStoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}