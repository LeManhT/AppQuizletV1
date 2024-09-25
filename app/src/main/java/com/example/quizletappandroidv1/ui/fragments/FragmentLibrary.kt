package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.appquizlet.adapter.ViewPagerLibAdapter
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.FragmentLibraryBinding
import com.example.quizletappandroidv1.viewmodel.studyset.DocumentViewModel
import com.google.android.material.tabs.TabLayoutMediator

class FragmentLibrary : Fragment() {
    private lateinit var binding: FragmentLibraryBinding
    private lateinit var navController: NavController
    private lateinit var adapter: ViewPagerLibAdapter
    private val documentViewModel: DocumentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibraryBinding.inflate(layoutInflater, container, false)

// Set up NavController using the fragment's parent fragment manager
        navController = findNavController()

// Set up ViewPager and TabLayout
        setupViewPager()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupViewPager() {
// Create the adapter
        adapter = ViewPagerLibAdapter(childFragmentManager, lifecycle)

// Set up the ViewPager2 with the adapter
        binding.pagerLib.adapter = adapter

// Link the TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLib, binding.pagerLib) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.lb_study_sets)
                1 -> tab.text = getString(R.string.folders)
            }
        }.attach()

// Handle tab changes and trigger navigation
//        binding.pagerLib.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                // Navigate to the appropriate fragment based on tab selection
//                when (position) {
//                    0 -> navController.navigate(R.id.fragmentStudySets)
//                    1 -> navController.navigate(R.id.fragmentFolders)
//                }
//            }
//        })
    }


}