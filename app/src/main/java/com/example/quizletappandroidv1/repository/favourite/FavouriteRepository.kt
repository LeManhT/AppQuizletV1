package com.example.quizletappandroidv1.repository.favourite

import androidx.lifecycle.LiveData
import com.example.quizletappandroidv1.dao.FavouriteDao
import com.example.quizletappandroidv1.models.NewWord
import javax.inject.Inject

class FavouriteRepository @Inject constructor(private val favouriteDao: FavouriteDao) {
    suspend fun addFavouriteWord(word: String, meaning: String): Long {
        val favouriteWord = NewWord(0, word = word, meaning = meaning, false)
        return favouriteDao.insertFavouriteWord(favouriteWord)
    }

    fun getAllFavouriteWords(): LiveData<List<NewWord>> {
        return favouriteDao.getAllFavouriteWords()
    }

    suspend fun deleteFavouriteWord(favouriteWord: NewWord) {
        favouriteDao.deleteFavouriteWord(favouriteWord.id)
    }

}