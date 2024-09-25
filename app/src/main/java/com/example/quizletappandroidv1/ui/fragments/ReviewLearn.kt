package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.quizletappandroidv1.MyApplication
import com.example.quizletappandroidv1.adapter.ReviewLearnAdapter
import com.example.quizletappandroidv1.databinding.FragmentReviewLearnBinding
import com.example.quizletappandroidv1.models.FlashCardModel
import com.example.quizletappandroidv1.viewmodel.user.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewLearn : Fragment() {
    private var _binding: FragmentReviewLearnBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterReviewLearn: ReviewLearnAdapter
    private lateinit var listCards: MutableList<FlashCardModel>
    private val userViewModel: UserViewModel by viewModels()
    private val args: ReviewLearnArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewLearnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MyApplication.userId?.let { userViewModel.getUserData(it) }

        listCards = mutableListOf()

        userViewModel.userData.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                val targetId = args.setId
                val studySetTarget = it.documents.studySets.find { studySet ->
                    studySet.id == targetId
                }
                if (studySetTarget != null) {
                    listCards.clear()
                    listCards.addAll(studySetTarget.cards)
                    adapterReviewLearn.updateData(studySetTarget.cards)
                }

            }.onFailure { }
        }

        adapterReviewLearn = ReviewLearnAdapter(requireContext(), binding.rvReviewLearn)
        val myLinearLayoutManager =
            object : LinearLayoutManager(requireContext(), HORIZONTAL, false) {

            }

        binding.rvReviewLearn.layoutManager = myLinearLayoutManager
        binding.rvReviewLearn.adapter = adapterReviewLearn

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvReviewLearn)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
