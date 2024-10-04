package com.example.quizletappandroidv1.viewmodel.quote

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizletappandroidv1.custom.CustomToast
import com.example.quizletappandroidv1.entity.QuoteEntity
import com.example.quizletappandroidv1.models.QuoteResponse
import com.example.quizletappandroidv1.repository.quote.QuoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(private val quoteRepository: QuoteRepository) :
    ViewModel() {
    //    val dataQuote: MutableLiveData<List<QuoteModel>> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    private var currentPosition = 0
    fun getCurrentPosition(): Int {
        return currentPosition
    }

    fun setCurrentPosition(position: Int) {
        currentPosition = position
    }

    fun getNextQuotePosition(): Int {
        return if (currentPosition < quotes.value?.results?.size!!) {
            currentPosition + 1
        } else {
            currentPosition
        }
    }

    fun getPrevQuotePosition(): Int {
        return if (currentPosition > 0) {
            currentPosition - 1
        } else {
            currentPosition
        }
    }

    val quotes: LiveData<QuoteResponse>
        get() = quoteRepository.quotes

    val localQuote: LiveData<List<QuoteEntity>>
        get() = quoteRepository.localQuotes

    //    init {
    fun getRemoteQuote(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            // Set isLoading to true before making the network call
            isLoading.postValue(true)

            try {
                quoteRepository.getQuotes(1)
            } catch (e: Exception) {
                CustomToast(context).makeText(
                    context,
                    e.message.toString(),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            } finally {
                // Set isLoading to false after the network call is complete
                isLoading.postValue(false)
            }
        }
    }
//    }

    fun getLocalQuotes(userId: String) {
        Log.d("getLocalQuotes", userId.toString())
        viewModelScope.launch(Dispatchers.IO) {
            quoteRepository.getLocalQuotes(userId)
        }
    }

    fun insertQuote(quoteModel: QuoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            quoteRepository.insertQuote(quoteModel)
        }
    }

    fun deleteQuote(quoteId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            quoteRepository.deleteLocalQuote(quoteId)
        }
    }

}