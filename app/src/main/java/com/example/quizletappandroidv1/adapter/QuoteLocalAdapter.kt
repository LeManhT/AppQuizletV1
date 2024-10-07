package com.example.quizletappandroidv1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletappandroidv1.databinding.QuoteItemWithDeleteBinding
import com.example.quizletappandroidv1.entity.QuoteEntity

class QuoteLocalAdapter(
    private val recyclerView: RecyclerView,
    private val onQuotifyListener: OnQuotifyLocalListener,
) : RecyclerView.Adapter<QuoteLocalAdapter.QuotifyViewHolder>() {

    private var quotes: List<QuoteEntity> = mutableListOf()

    interface OnQuotifyLocalListener {
        fun handleShareQuote(position: Int)
        fun handleDeleteQuote(quoteId: Long)
        fun handleNextQuote(position: Int)
        fun handlePrevQuote(position: Int)
    }

    class QuotifyViewHolder(val binding: QuoteItemWithDeleteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuotifyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = QuoteItemWithDeleteBinding.inflate(inflater, parent, false)
        return QuotifyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuotifyViewHolder, position: Int) {
        val item = quotes[position]
        holder.binding.quoteText.text = item.content
        holder.binding.quoteAuthor.text = item.author
        holder.binding.btnShareQuote.setOnClickListener {
            onQuotifyListener.handleShareQuote(position)
        }

        holder.binding.btnDeleteQuote.setOnClickListener {
            onQuotifyListener.handleDeleteQuote(item.quoteId)
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return quotes.size
    }

    fun updateQuotes(newQuotes: List<QuoteEntity>) {
        quotes = newQuotes
        notifyDataSetChanged()
    }


    fun handleNextQuote(currentPosition: Int): Int {
        val nextPosition =
            if (currentPosition < quotes.size - 1) currentPosition + 1 else currentPosition
        recyclerView.smoothScrollToPosition(nextPosition)
        return nextPosition
    }

    fun handlePrevQuote(currentPosition: Int): Int {
        val prevPosition = if (currentPosition > 0) currentPosition - 1 else currentPosition
        recyclerView.smoothScrollToPosition(prevPosition)
        return prevPosition
    }

    fun getQuoteText(position: Int): List<String?> {
        val item = quotes[position]
        return listOf(item.content, item.author)
    }


}