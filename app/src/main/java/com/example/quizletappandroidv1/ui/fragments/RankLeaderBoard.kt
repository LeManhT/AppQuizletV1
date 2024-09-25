package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.RankItemAdapter
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.customview.RankView
import com.example.quizletappandroidv1.databinding.FragmentRankLeaderBoardBinding
import com.example.quizletappandroidv1.viewmodel.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankLeaderBoard : Fragment() {
    private lateinit var binding: FragmentRankLeaderBoardBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapterItemRank: RankItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRankLeaderBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rankView = view.findViewById<RankView>(R.id.rankView)

        // Set ranks, these could be fetched from an API or database
        rankView.setTopRanks(
            "1.66 points",
            "391.19K points",
            "309.07K points",
            R.raw.top1_removebg_preview,
            R.raw.t2_removebg_preview,
            R.raw.t3_removebg_preview
        )

        adapterItemRank = RankItemAdapter(requireContext())

        homeViewModel.rankResult.observe(viewLifecycleOwner) { userResponse ->
            userResponse.onSuccess {
                // Set other ranks
                val otherRanks = listOf(
                    it,
                    it,
                    it,
                )
                rankView.setOtherRanks(otherRanks)
                adapterItemRank.updateData(it.rankSystem.userRanking)
                (it.currentRank + 1).toString().also { binding.txtMyOrder.text = it }
                binding.txtMyPoint.text = it.currentScore.toString()
                binding.txtMyPointGain.text = it.currentScore.toString()
                binding.txtTop1Name.text = it.rankSystem.userRanking[0].userName
                binding.txtTop1Point.text = it.rankSystem.userRanking[0].score.toString()
            }.onFailure {
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    it.message.toString(),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            }
        }
    }
}