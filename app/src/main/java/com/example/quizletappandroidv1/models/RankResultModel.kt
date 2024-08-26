package com.example.quizletappandroidv1.models
data class RankSystem(
    val userRanking: List<RankItemModel>
)

data class RankResultModel(
    val currentScore: Int,
    val currentRank: Int,
    val rankSystem: RankSystem
)
