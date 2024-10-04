package com.example.quizletappandroidv1.ui.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.model.SearchSetModel
import com.example.quizletappandroidv1.databinding.FragmentSearchSetResultBinding
import com.example.quizletappandroidv1.listener.RvClickSearchSet
import com.example.quizletappandroidv1.viewmodel.studyset.DocumentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentSearchSetResult : Fragment() {
    private lateinit var progressDialog: ProgressDialog
    private var listSearch = mutableListOf<SearchSetModel>()
    private lateinit var binding: FragmentSearchSetResultBinding
    private val documentViewModel: DocumentViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchSetResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        dataSearch.observe(viewLifecycleOwner) {
//            if (it != null) {
//                listSearch.clear()
//                listSearch.addAll(it)
//                if (listSearch.isEmpty()) {
//                    binding.layoutHasData.visibility = View.GONE
//                    binding.layoutNoData.visibility = View.VISIBLE
//                } else {
//                    binding.layoutHasData.visibility = View.VISIBLE
//                    binding.layoutNoData.visibility = View.GONE
//                    val solutionItemAdapter = SolutionItemAdapter(listSearch, object :
//                        RvClickSearchSet {
//                        override fun handleClickSetSearch(position: Int) {
////                            val intent = Intent(context, SearchDetail::class.java)
////                            intent.putExtra("setId", listSearch[position].id)
////                            startActivity(intent)
//                        }
//                    })
//                    binding.rvSolution.layoutManager =
//                        LinearLayoutManager(
//                            context,
//                            LinearLayoutManager.VERTICAL,
//                            false
//                        )
//                    binding.rvSolution.adapter = solutionItemAdapter
//                }
//            }
//        }
    }

}