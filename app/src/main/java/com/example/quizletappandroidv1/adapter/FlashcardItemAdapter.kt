package com.example.quizletappandroidv1.adapter

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.models.FlashCardModel

class FlashcardItemAdapter(
    private var itemClickListener: OnFlashcardItemClickListener? = null
) : RecyclerView.Adapter<FlashcardItemAdapter.FlashcardItemHolder>() {
    class FlashcardItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    interface OnFlashcardItemClickListener {
        fun onFlashcardItemClick(term: String)
    }

    private var listFlashcard: List<FlashCardModel> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_flashcard_detail, parent, false)
        return FlashcardItemHolder(view)
    }

    override fun onBindViewHolder(holder: FlashcardItemHolder, position: Int) {
        val txtTerm = holder.itemView.findViewById<TextView>(R.id.txtFlashcardDetailTerm)
        val txtDefinition = holder.itemView.findViewById<TextView>(R.id.txtFlashcardDetailDefinition)

        txtTerm.text = listFlashcard[position].term
        txtDefinition.text = listFlashcard[position].definition

        holder.itemView.setOnClickListener {
            // Đọc listFlashcard[position].term ngay lập tức
            itemClickListener?.onFlashcardItemClick(listFlashcard[position].term.toString())

            // Tạo một delay 1 giây trước khi đọc listFlashcard[position].definition
            Handler().postDelayed({
                itemClickListener?.onFlashcardItemClick(listFlashcard[position].definition.toString())
            }, 1000) // 1000 milliseconds = 1 second
        }


    }

    fun updateData(newList: List<FlashCardModel>) {
        this.listFlashcard = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listFlashcard.size
    }
    fun setOnFlashcardItemClickListener(listener: OnFlashcardItemClickListener) {
        this.itemClickListener = listener
    }
}
