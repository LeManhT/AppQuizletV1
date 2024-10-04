package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizletappandroidv1.adapter.StoryAdapter
import com.example.quizletappandroidv1.databinding.FragmentSolutionListBinding
import com.example.quizletappandroidv1.models.Story
import com.example.quizletappandroidv1.utils.Helper

class SolutionListFragment : Fragment() {
    private lateinit var binding: FragmentSolutionListBinding
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSolutionListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stories = Helper.getStories(requireContext())
        storyAdapter = StoryAdapter(stories,object : StoryAdapter.IStoryClick {
            override fun handleStoryClick(story: Story) {
                val action = SolutionListFragmentDirections.actionSolutionListFragmentToFragmentSolution(story)
                findNavController().navigate(action)
            }
        })

        binding.rvListSolutionImages.adapter = storyAdapter
        binding.rvListSolutionImages.layoutManager = LinearLayoutManager(context)

    }

}