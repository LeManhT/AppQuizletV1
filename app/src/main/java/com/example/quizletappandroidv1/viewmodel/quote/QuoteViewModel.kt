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
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val quotes: LiveData<QuoteResponse>
        get() = quoteRepository.quotes

    val localQuote: LiveData<List<QuoteEntity>>
        get() = quoteRepository.localQuotes

    fun getRemoteQuote() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            try {
                quoteRepository.getQuotes(1)
            } catch (e: Exception) {
//                CustomToast(context).makeText(
//                    context,
//                    e.message.toString(),
//                    CustomToast.LONG,
//                    CustomToast.ERROR
//                ).show()
                Log.d("ToastQuote", "Error: ${e.message}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun getLocalQuotes(userId: String) {
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