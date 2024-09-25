package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.SolutionImageAdapter
import com.example.quizletappandroidv1.databinding.FragmentSolutionListBinding

class SolutionListFragment : Fragment() {
    private lateinit var binding: FragmentSolutionListBinding

    private val imageResources = listOf(
        R.raw.ac101,
        R.raw.many_onj,
        R.raw.ac104
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSolutionListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        val imageAdapter = SolutionImageAdapter() { imageResource ->
            // Navigate to the image details when an image is clicked
            val action =
                SolutionListFragmentDirections.actionSolutionListFragmentToFragmentSolution(imageResource)
            findNavController().navigate(action)
        }

        binding.rvListSolutionImages.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = imageAdapter
        }

        imageAdapter.updateData(imageResources)

    }


}