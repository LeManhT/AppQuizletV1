package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.AdapterSearchSet
import com.example.quizletappandroidv1.databinding.FragmentSearchBinding
import com.example.quizletappandroidv1.viewmodel.studyset.DocumentViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentSearch : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val documentViewModel: DocumentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapterLibPager = AdapterSearchSet(parentFragmentManager, lifecycle)
        binding.pagerLib.adapter = adapterLibPager
        TabLayoutMediator(binding.tabLib, binding.pagerLib) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.text = resources.getString(R.string.all_results)
                    tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.all_apps, null)
                    val badge = tab.orCreateBadge
                    badge.backgroundColor =
                        ResourcesCompat.getColor(resources, R.color.semi_blue, null)
                }

                1 -> {
                    tab.text = resources.getString(R.string.sets)
                    tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.note, null)
                    val badge = tab.orCreateBadge
                    badge.backgroundColor =
                        ResourcesCompat.getColor(resources, R.color.semi_blue, null)
                }

                2 -> {
                    tab.text = resources.getString(R.string.user)
                    tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.user, null)
                    val badge = tab.orCreateBadge
                    badge.backgroundColor =
                        ResourcesCompat.getColor(resources, R.color.semi_blue, null)
                    badge.number = 0
                }
            }
        }.attach()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    documentViewModel.findStudySet(query)
                    adapterLibPager.updateAllResultsData(query)
                    findNavController().navigate(R.id.action_fragmentSearch2_to_fragmentAllResult)
                }
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })


    }


}