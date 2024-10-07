package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.model.SearchSetModel
import com.example.quizletappandroidv1.adapter.SearchListAdapter
import com.example.quizletappandroidv1.adapter.SearchListAdapter.ISearchSetClick
import com.example.quizletappandroidv1.databinding.FragmentAllResultBinding
import com.example.quizletappandroidv1.entity.UserResponse
import com.example.quizletappandroidv1.viewmodel.studyset.DocumentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentAllResult : Fragment() {
    private lateinit var binding: FragmentAllResultBinding
    private var listSearch = mutableListOf<SearchSetModel>()
    private var listUser = mutableListOf<UserResponse>()
    private val documentViewModel: DocumentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllResultBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        documentViewModel.studySetSearchResults.observe(viewLifecycleOwner) {
            listSearch.clear()
            listSearch.addAll(it)
            if (listUser.isEmpty() && listSearch.isEmpty()) {
                binding.layoutHasData.visibility = View.GONE
                binding.layoutNoData.visibility = View.VISIBLE
            } else {
                binding.layoutHasData.visibility = View.VISIBLE
                binding.layoutNoData.visibility = View.GONE
            }
            if (listSearch.isEmpty()) {
                binding.layoutSet.visibility = View.GONE
            } else {
                binding.layoutSet.visibility = View.VISIBLE
                val solutionItemAdapter =
                    SearchListAdapter(object : ISearchSetClick {
                        override fun handleSearchSetClick(studyset: SearchSetModel) {
//                            val action =
//                                FragmentALlResultDirections.actionFragmentHome3ToStudySetDetail(
//                                    studyset.id
//                                )
//                            findNavController().navigate(action)
                        }
                    })
                binding.rvSearchStudySet.layoutManager =
                    LinearLayoutManager(
                        context,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                binding.rvSearchStudySet.adapter = solutionItemAdapter
                solutionItemAdapter.updateData(listSearch)

//                binding.txtStudySetViewAll.setOnClickListener {
//                    (activity as SplashSearch).binding.pagerLib.currentItem = 1
//                }
//                binding.txtFolderViewAll.setOnClickListener {
//                    (activity as SplashSearch).binding.pagerLib.currentItem = 2
//                }
            }

            if (listUser.isEmpty()) {
                binding.layoutUser.visibility = View.GONE
            }

        }
    }

    fun updateData(query: String) {

    }

}