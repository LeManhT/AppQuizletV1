package com.example.quizletappandroidv1.adapter

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.quizletappandroidv1.R
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.databinding.LayoutQuestionReviewBinding
import com.example.quizletappandroidv1.models.FlashCardModel
import com.example.quizletappandroidv1.ui.fragments.ReviewLearnDirections
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson

class ReviewLearnAdapter(
    private val context: Context,
    private val recyclerView: RecyclerView
) :
    RecyclerView.Adapter<ReviewLearnAdapter.ReviewLearnHolder>() {
    private var selectedAnswer: String? = null
    private var isQuestionAnswered = false
    private var selectedCardView: CardView? = null
    private var countFalse: Int? = 0
    private var countTrue: Int? = 0
    private var countAnswer: Int = 0

    private var listFlashcards: List<FlashCardModel> = mutableListOf()


    inner class ReviewLearnHolder(val binding: LayoutQuestionReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var countDownTimer: CountDownTimer? = null
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewLearnHolder {
        val view = LayoutQuestionReviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReviewLearnHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewLearnHolder, position: Int) {


        val progressBar: ProgressBar = holder.itemView.findViewById(R.id.questionProgressBar)
        val tvTimer: TextView = holder.itemView.findViewById(R.id.tvTimer)

        // Cancel any previous timer when a new item is bound
        holder.countDownTimer?.cancel()

        val totalTime = 10000L // 10 seconds
        progressBar.max = (totalTime / 1000).toInt()
        progressBar.progress = progressBar.max

        holder.countDownTimer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                tvTimer.text = "0:$secondsLeft"
                progressBar.progress = secondsLeft.toInt()
            }

            override fun onFinish() {
                // Handle when time runs out and no answer was provided
                tvTimer.text = "0:00"
                progressBar.progress = 0
                handleTimeOut(holder)
            }
        }.start()




        val question: TextView = holder.binding.tvQuestion
        question.text = listFlashcards[position].term.toString()
        val options: List<FlashCardModel>
        val textOptionCard1: TextView = holder.binding.answer1TextView
        val textOptionCard2: TextView = holder.binding.answer2TextView
        val textOptionCard3: TextView = holder.binding.answer3TextView

        // Shuffle options randomly
        val correctAnswer: String = listFlashcards[position].definition.toString()
        val incorrectOptions = listFlashcards.shuffled().filter { it.definition != correctAnswer }
        options = (incorrectOptions.take(2) + listFlashcards[position]).shuffled()

        textOptionCard1.text = options[0].definition ?: ""
        textOptionCard2.text = options[1].definition ?: ""
        textOptionCard3.text = options[2].definition ?: ""

        holder.binding.cardViewAnswer1.setOnClickListener {
            handleAnswerSelection(holder, holder.binding.cardViewAnswer1, options[0], position)
        }

        holder.binding.cardViewAnswer2.setOnClickListener {
            handleAnswerSelection(holder, holder.binding.cardViewAnswer2, options[1], position)
        }

        holder.binding.cardViewAnswer3.setOnClickListener {
            handleAnswerSelection(holder, holder.binding.cardViewAnswer3, options[2], position)
        }
        holder.binding.submitButton.setOnClickListener {
//            if (!isQuestionAnswered) {
            Log.d("selectedAnswer", selectedAnswer.toString())
            if (selectedAnswer.isNullOrBlank()) {
                CustomToast(context).makeText(
                    context,
                    context.resources.getString(R.string.please_choose_answer),
                    CustomToast.LONG,
                    CustomToast.WARNING
                ).show()
            } else {
                if (listFlashcards[position].isAnswer == false) {
                    handleCheckAnswer(
                        holder,
                        context,
                        selectedCardView,
                        selectedAnswer,
                        correctAnswer
                    )
                    listFlashcards[position].isAnswer = true
                }
                isQuestionAnswered = true
            }
//            }
        }

    }

    private fun handleAnswerSelection(
        holder: ReviewLearnHolder,
        cardView: CardView,
        option: FlashCardModel,
        position: Int
    ) {
//        if (!isQuestionAnswered) {
        if (listFlashcards[position].isAnswer == false) {
            holder.binding.cardViewAnswer1.setCardBackgroundColor(
                ContextCompat.getColor(context, android.R.color.holo_orange_light)
            )
            holder.binding.cardViewAnswer2.setCardBackgroundColor(
                ContextCompat.getColor(context, android.R.color.holo_orange_light)
            )
            holder.binding.cardViewAnswer3.setCardBackgroundColor(
                ContextCompat.getColor(context, android.R.color.holo_orange_light)
            )
            cardView.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.semi_blue)
            )
            selectedAnswer = option.definition
            selectedCardView = cardView
        }
//        }
    }

    private fun handleCheckAnswer(
        holder: ReviewLearnHolder,
        context: Context,
        selectedCardView: CardView?,
        selectedAnswer: String?,
        correctAnswer: String
    ) {
        val isCorrect = selectedAnswer == correctAnswer
        highlight(context, selectedCardView, isCorrect)
        if (isCorrect) {
            showNiceDoneDialog()
            countTrue = countTrue!! + 1;
        } else {
            showIncorrectDialog(correctAnswer)
            countFalse = countFalse!! + 1;
        }
        holder.binding.cardViewAnswer1.setCardBackgroundColor(
            ContextCompat.getColor(context, android.R.color.holo_orange_light)
        )
        holder.binding.cardViewAnswer2.setCardBackgroundColor(
            ContextCompat.getColor(context, android.R.color.holo_orange_light)
        )
        holder.binding.cardViewAnswer3.setCardBackgroundColor(
            ContextCompat.getColor(context, android.R.color.holo_orange_light)
        )
        selectedCardView?.setCardBackgroundColor(
            ContextCompat.getColor(context, R.color.semi_blue)
        )
    }

    private fun highlight(context: Context, cardView: CardView?, isCorrect: Boolean) {
        if (isCorrect) {
            cardView?.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.semi_blue
                )
            )
        } else {
            cardView?.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.my_yellow
                )
            )
        }
    }

    private fun showNiceDoneDialog() {
//Trong Android, Context được sử dụng để truy cập tài nguyên của ứng dụng, chẳng hạn như chuỗi (string), hình ảnh, màu sắc, vv. Một số phương thức như getString cần một đối tượng Context để có thể truy cập đúng tài nguyên từ tập tin res/values/strings.xml hoặc các thư mục tương ứng.
        val customView = LayoutInflater.from(context).inflate(R.layout.custom_result_dialog, null)
        val dialog = MaterialAlertDialogBuilder(context)
//            .setTitle(context.resources.getString(R.string.cancel))
            .setView(customView)
            .setCancelable(false)
            .show()
        android.os.Handler().postDelayed({
            dialog.dismiss()
            scrollToNextQuestion()
            isQuestionAnswered = false
            selectedAnswer = null
        }, 1000)
    }

    private fun showIncorrectDialog(correctAnswer: String) {
        val customView = LayoutInflater.from(context).inflate(R.layout.custom_review_dialog, null)
        customView.findViewById<TextView>(R.id.txtYourChoose).text = selectedAnswer
        customView.findViewById<TextView>(R.id.txtCorrectAnswer).text = correctAnswer
        val dialog = MaterialAlertDialogBuilder(context)
            .setView(customView)
            .setCancelable(false)
            .show()

        val btnContinue: AppCompatButton = customView.findViewById(R.id.btnShuffle)
        btnContinue.setOnClickListener {
            scrollToNextQuestion()
            dialog.dismiss()
            isQuestionAnswered = false
            selectedAnswer = null
        }
    }

    override fun getItemCount(): Int {
        return listFlashcards.size
    }

    private fun scrollToNextQuestion() {
        countAnswer++
        val nextPosition = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)) + 1
        if (countAnswer >= listFlashcards.size) {
            val activity = recyclerView.context as? FragmentActivity
//            activity?.let {
                val navController = activity?.findNavController(R.id.nav_host_fragment)
                val action = ReviewLearnDirections.actionReviewLearnToExcellent2(
                    Gson().toJson(listFlashcards),
                    countTrue ?: 0,
                    countFalse ?: 0,
                    listFlashcards.size
                )
                navController?.navigate(action)
//            }
        }
        if (nextPosition < listFlashcards.size) {
            recyclerView.smoothScrollToPosition(nextPosition)
            notifyItemChanged(nextPosition)
        } else {
            if (countAnswer < listFlashcards.size) {
                CustomToast(context).makeText(
                    context,
                    context.resources.getString(R.string.please_answer_all_question),
                    CustomToast.LONG,
                    CustomToast.WARNING
                ).show()
            }
        }
    }

    fun updateData(newData: List<FlashCardModel>) {
        this.listFlashcards = newData
        notifyDataSetChanged()
    }

    private fun handleTimeOut(holder: ReviewLearnHolder) {
        if (listFlashcards[holder.bindingAdapterPosition].isAnswer == false) {
            val correctAnswer = listFlashcards[holder.bindingAdapterPosition].definition.toString()
            showIncorrectDialog(correctAnswer)
            countFalse = (countFalse ?: 0) + 1
            listFlashcards[holder.bindingAdapterPosition].isAnswer = true
        }
    }


}