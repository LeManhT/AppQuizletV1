package com.example.quizletappandroidv1.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quizletappandroidv1.models.NewWord

@Dao
interface FavouriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouriteWord(favouriteWord: NewWord): Long

    @Query("SELECT * FROM favourite_words")
    fun getAllFavouriteWords(): LiveData<List<NewWord>>
}