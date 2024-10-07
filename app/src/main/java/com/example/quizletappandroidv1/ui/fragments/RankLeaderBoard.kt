package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.adapter.RankItemAdapter
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.customview.RankView
import com.example.quizletappandroidv1.databinding.FragmentRankLeaderBoardBinding
import com.example.quizletappandroidv1.models.RankItemModel
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

        adapterItemRank = RankItemAdapter(requireContext(),object : RankItemAdapter.IClickRankItemModel {
            override fun showAchievementDialog(rankItemModel: RankItemModel) {
                val action = RankLeaderBoardDirections.actionRankLeaderBoardToItemRankDetail(rankItemModel)
                findNavController().navigate(action)
            }
        })
        binding.rvLeaderboard.apply {
            adapter = adapterItemRank
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        }

        homeViewModel.rankResult.observe(viewLifecycleOwner) { userResponse ->
            userResponse.onSuccess {
                val otherRanks = listOf(
                    it,
                    it,
                    it,
                )
//                rankView.setOtherRanks(otherRanks)
                adapterItemRank.updateData(it.rankSystem.userRanking)
//                (it.currentRank + 1).toString().also { binding.txtMyOrder.text = it }
//                binding.txtMyPoint.text = it.currentScore.toString()
//                binding.txtMyPointGain.text = it.currentScore.toString()
//                binding.txtTop1Name.text = it.rankSystem.userRanking[0].userName
//                binding.txtTop1Point.text = it.rankSystem.userRanking[0].score.toString()
                rankView.setTopRanks(
                    it.rankSystem.userRanking[0].userName,
                    it.rankSystem.userRanking[1].userName,
                    it.rankSystem.userRanking[2].userName,
                    R.raw.top1_removebg_preview,
                    R.raw.t2_removebg_preview,
                    R.raw.t3_removebg_preview
                )
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