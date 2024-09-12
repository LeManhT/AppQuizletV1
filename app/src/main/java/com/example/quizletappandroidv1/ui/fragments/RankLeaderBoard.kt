package com.example.quizletappandroidv1.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.customview.RankView
import com.example.quizletappandroidv1.models.RankItemModel
import com.example.quizletappandroidv1.models.RankResultModel
import com.example.quizletappandroidv1.models.RankSystem

class RankLeaderBoard : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rank_leader_board, container, false)
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

        // Set other ranks
        val otherRanks = listOf(
           RankResultModel(100, 1,RankSystem(listOf(RankItemModel(21,1,"lemanh","lemanh@gmail.com","05/09/2002",1)))),
           RankResultModel(100, 2,RankSystem(listOf(RankItemModel(21,1,"lemanh","lemanh@gmail.com","05/09/2002",1)))),
           RankResultModel(100, 3,RankSystem(listOf(RankItemModel(21,1,"lemanh","lemanh@gmail.com","05/09/2002",1)))),
        )
        rankView.setOtherRanks(otherRanks)
    }


}