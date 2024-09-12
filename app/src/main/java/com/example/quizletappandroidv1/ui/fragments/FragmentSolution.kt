package com.example.quizletappandroidv1.ui.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.customview.ClickablePoint
import com.example.quizletappandroidv1.databinding.FragmentSolutionBinding

class FragmentSolution : Fragment() {
    private lateinit var binding: FragmentSolutionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSolutionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val image = BitmapFactory.decodeResource(resources, R.raw.ac101)
        val points = listOf(
            ClickablePoint(200f, 300f, "Beach Umbrella", "Dù biển"),
            ClickablePoint(400f, 500f, "Life Buoy", "Phao cứu sinh")
        )
        binding.customImageView.setImagePoints(image, points)

    }

}