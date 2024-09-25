package com.example.quizletappandroidv1.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.RankLeaderBoardItemBinding
import com.example.quizletappandroidv1.models.RankItemModel
import com.example.quizletappandroidv1.utils.Helper

class RankItemAdapter(
    private val context: Context,
) : RecyclerView.Adapter<RankItemAdapter.RankItemHolder>() {
    private var listItemRank: List<RankItemModel> = mutableListOf()
    var isExpanded: Boolean = false

    inner class RankItemHolder(val binding: RankLeaderBoardItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankItemHolder {
        return RankItemHolder(
            RankLeaderBoardItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RankItemHolder, position: Int) {
        val currentItem = listItemRank[position]
        val txtOrder = holder.binding.txtOrder
        val txtTopName = holder.binding.txtTopName
        val txtPoint = holder.binding.txtTopPoint
        val imgAvatar = holder.binding.imgAvatar
        val imgTopOrder = holder.binding.imgTopOrder
        listItemRank[position].order = position + 1
        val imageName = "medal_${position + 1}"
        val imageResourceId =
            context.resources.getIdentifier(imageName, "raw", context.packageName)
        if (position == 0 || position == 1 || position == 2) {
            imgTopOrder.visibility = View.VISIBLE
            imgTopOrder.setImageResource(imageResourceId)
            txtOrder.visibility = View.GONE
        }
        if(Helper.getDataUsername(context) == listItemRank[position].userName || Helper.getDataUsername(context) == listItemRank[position].email) {
            txtTopName.text = context.getString(R.string.me)
        } else {
            txtTopName.text = currentItem.userName
        }
        txtPoint.text = currentItem.score.toString()
        Log.d("TAG", "onBindViewHolder: ${listItemRank[position].order} position : ${position}")
        (position + 1).toString().also { txtOrder.text = it }
//        imgAvatar.setImageResource(currentItem.topUserImage)

        holder.itemView.apply {
            setOnClickListener {
                showAchievementDialog(listItemRank[position])
            }
        }
    }


    override fun getItemCount(): Int {
//        return if (isExpanded) {
//            listItemRank.size
//        }
//        else {
//            3
//        }
        return listItemRank.size
    }

    fun setIsExpanded() {
        isExpanded = isExpanded.not()
    }

    private fun showAchievementDialog(rankItemModel: RankItemModel) {
//        val addBottomSheet = ItemUserRankDetail.newInstance(rankItemModel)
//        addBottomSheet.show(
//            (context as AppCompatActivity).supportFragmentManager,
//            addBottomSheet.tag
//        )
    }

    fun updateData(list: List<RankItemModel>) {
        listItemRank = list
        notifyDataSetChanged()
    }
}
