package com.example.quizletappandroidv1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.models.NewWord

class NewWordAdapter(
    private val onTranslateClicked: INewWordClick? = null
) : RecyclerView.Adapter<NewWordAdapter.NewWordViewHolder>() {

    private var newWords: List<NewWord> = mutableListOf()

    interface INewWordClick {
        fun handleTranslateClick(newWord: NewWord, position: Int, view: View)
        fun handleAddToFavourite(newWord: NewWord)
    }

    inner class NewWordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordTextView: TextView = itemView.findViewById(R.id.wordTextView)
        val translatedMeaningTextView: TextView =
            itemView.findViewById(R.id.translatedMeaningTextView)
        val translateButton: ImageButton = itemView.findViewById(R.id.translateButton)
        val btnAddToFavourite: ImageButton = itemView.findViewById(R.id.btnAddToFavourite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewWordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_new_word, parent, false)
        return NewWordViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewWordViewHolder, position: Int) {
        val newWord = newWords[position]
        holder.wordTextView.text = newWord.word
        holder.translatedMeaningTextView.text = newWord.meaning


        holder.translateButton.setOnClickListener { view ->
            onTranslateClicked?.handleTranslateClick(newWord, position, view)
        }

        holder.btnAddToFavourite.setOnClickListener {
            onTranslateClicked?.handleAddToFavourite(newWord)
        }

    }

    override fun getItemCount(): Int {
        return newWords.size
    }

    fun updateData(newWordList: List<NewWord>) {
        this.newWords = newWordList
        notifyDataSetChanged()
    }

}