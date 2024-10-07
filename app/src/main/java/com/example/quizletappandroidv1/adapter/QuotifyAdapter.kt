import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletappandroidv1.databinding.QuotifyItemBinding
import com.example.quizletappandroidv1.entity.QuoteEntity

class QuotifyAdapter(
    private val recyclerView: RecyclerView,
    private var onQuotifyListener: OnQuotifyListener? = null,
) : RecyclerView.Adapter<QuotifyAdapter.QuotifyViewHolder>() {
    private var quotes: List<QuoteEntity> = mutableListOf()

    interface OnQuotifyListener {
        fun handleShareQuote(position: Int)
        fun handleAddToMyQuote(position: Int)
    }

    class QuotifyViewHolder(val binding: QuotifyItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuotifyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = QuotifyItemBinding.inflate(inflater, parent, false)
        return QuotifyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuotifyViewHolder, position: Int) {
        val item = quotes[position]
        holder.binding.quoteText.text = item.content
        holder.binding.quoteAuthor.text = item.author

        holder.binding.btnShareQuote.setOnClickListener {
            onQuotifyListener?.handleShareQuote(position)
        }

        holder.binding.btnSavedQuote.setOnClickListener {
            onQuotifyListener?.handleAddToMyQuote(position)
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
    fun setOnQuoteShareListener(listener: OnQuotifyListener) {
        onQuotifyListener = listener
    }
}