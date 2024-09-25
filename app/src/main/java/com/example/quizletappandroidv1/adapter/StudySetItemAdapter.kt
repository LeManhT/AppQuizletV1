package com.example.appquizlet.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.interfaceFolder.RvFlashCard
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.models.FlashCardModel
import com.example.quizletappandroidv1.utils.Helper

class StudySetItemAdapter(
    private val onClickFlashCard: RvFlashCard,
    private var onClickBtnZoomListener: ClickZoomListener? = null
) : RecyclerView.Adapter<StudySetItemAdapter.StudySetItemHolder>() {
    class StudySetItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface ClickZoomListener {
        fun onClickZoomBtn()
    }

    private var listStudySet: List<FlashCardModel> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudySetItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_flashcard_item, parent, false)
        return StudySetItemHolder(view)
    }

    override fun onBindViewHolder(holder: StudySetItemHolder, position: Int) {
        val currentItem = listStudySet[position]
        holder.itemView.apply {
            val txtTerm = findViewById<TextView>(R.id.txtFlashcardTerm)
            val txtDefinition = findViewById<TextView>(R.id.txtFlashcardDefinition)
            val imgZoom = findViewById<ImageView>(R.id.imgZoom)
            txtTerm.text = currentItem.term
            txtDefinition.text = currentItem.definition
            val cardView = holder.itemView.findViewById<CardView>(R.id.frontFlashCardCardView)
            setOnClickListener {
                onClickFlashCard.handleClickFLashCard(currentItem)
                Helper.flipCard(cardView, txtTerm, currentItem)
                notifyItemChanged(position)
            }
            imgZoom.setOnClickListener {
                onClickBtnZoomListener?.onClickZoomBtn()
            }

        }
    }


    override fun getItemCount(): Int {
        return listStudySet.size
    }

    fun updateData(newList: List<FlashCardModel>) {
        listStudySet = newList
        notifyDataSetChanged()
    }

    fun setOnClickZoomBtnListener (listener : ClickZoomListener) {
        this.onClickBtnZoomListener = listener
    }
}