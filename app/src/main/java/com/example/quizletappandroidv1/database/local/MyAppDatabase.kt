package com.example.quizletappandroidv1.database.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.quizletappandroidv1.dao.FavouriteDao
import com.example.quizletappandroidv1.dao.QuoteDao
import com.example.quizletappandroidv1.entity.QuoteEntity
import com.example.quizletappandroidv1.models.NewWord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [QuoteEntity::class, NewWord::class],
    version = 1,
    exportSchema = true
)
abstract class MyAppDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
    abstract fun favouriteNewWordDao(): FavouriteDao

    companion object {
        @Volatile
        private var instance: MyAppDatabase? = null

        fun getInstance(context: Context): MyAppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, MyAppDatabase::class.java, "quotes.db")
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val applicationScope = CoroutineScope(Dispatchers.IO)
                        applicationScope.launch {

                        }
                    }
                })
                .build()
    }
}