package com.example.quizletappandroidv1.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.appquizlet.interfaceFolder.RVStudySetItem
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.databinding.LayoutCreateSetItemBinding
import com.example.quizletappandroidv1.models.StudySetModel
import com.example.quizletappandroidv1.utils.Helper
import com.google.gson.Gson

class RvStudySetItemAdapter(
    private val context: Context,
    private val onStudySetItem: RVStudySetItem,
    private val enableSwipe: Boolean,
    private val isCheckBackground: Boolean? = false
) : RecyclerView.Adapter<RvStudySetItemAdapter.StudySetItemHolder>() {

    private val viewBinderHelper: ViewBinderHelper = ViewBinderHelper()
    private var onClickSet: onClickSetItem? = null
    private val listStudySet = mutableListOf<StudySetModel>()

    interface onClickSetItem {
        fun handleClickDelete(setId: String)
    }

    inner class StudySetItemHolder(val binding: LayoutCreateSetItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudySetItemHolder {

        return StudySetItemHolder(
            LayoutCreateSetItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StudySetItemHolder, position: Int) {
        val currentItem = listStudySet[position]
        viewBinderHelper.closeLayout(listStudySet[position].timeCreated.toString())

        if (shouldEnableSwipeForItem() == true) {
            viewBinderHelper.bind(
                holder.binding.swipeRevealLayout,
                listStudySet[position].timeCreated.toString()
            )
        } else {
            holder.binding.swipeRevealLayout.close(false)
        }


        holder.binding.btnDeleteCard.setOnClickListener {
            onClickSet?.handleClickDelete(listStudySet[position].id)
        }


        val txtStudySetTitle = holder.binding.txtStudySetTitle
        val studySetChip = holder.binding.studySetChip
        val imgStudySetAvatar = holder.binding.imgStudySetAvatar
        val txtStudySetUsername = holder.binding.txtStudySetUsername
        val cardViewStudySet = holder.binding.studySetCardView

        val countTermText =
            if (currentItem.countTerm!! > 1) "${currentItem.countTerm} terms" else
                "${currentItem.countTerm} term"

        txtStudySetTitle.text = currentItem.name
        studySetChip.text = countTermText
        txtStudySetUsername.text = Helper.getDataUsername(context)
//            imgStudySetAvatar.setImageResource(currentItem.avatar)
            txtStudySetUsername.text = currentItem.nameOwner

        if (currentItem.folderOwnerId.isNotEmpty() && isCheckBackground == true) {
            cardViewStudySet.background =
                ContextCompat.getDrawable(context, R.drawable.selected_item_border)
            cardViewStudySet.alpha = 0.8F
        } else {
            cardViewStudySet.background =
                ContextCompat.getDrawable(context, R.drawable.bg_white)
        }


        if (currentItem.isSelected == true) {
            cardViewStudySet.background =
                ContextCompat.getDrawable(context, R.drawable.selected_item_border)
            cardViewStudySet.alpha = 0.8F
        } else {
            cardViewStudySet.background =
                ContextCompat.getDrawable(context, R.drawable.bg_white)
        }

        // Set item click listener
        cardViewStudySet.setOnClickListener {
            onStudySetItem.handleClickStudySetItem(currentItem, position)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return listStudySet.size
    }

    private fun shouldEnableSwipeForItem(): Boolean? {
        return enableSwipe
    }

    fun setOnItemClickListener(listener: onClickSetItem) {
        this.onClickSet = listener
    }

    fun updateData(newData: List<StudySetModel>) {
        listStudySet.clear()
        Log.d("newData", Gson().toJson(newData))
        listStudySet.addAll(newData)
        notifyDataSetChanged()
    }

}
