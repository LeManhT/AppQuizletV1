package com.example.quizletappandroidv1.di

import android.content.Context
import com.example.quizletappandroidv1.dao.QuoteDao
import com.example.quizletappandroidv1.database.api.retrofit.ApiService
import com.example.quizletappandroidv1.database.local.MyAppDatabase
import com.example.quizletappandroidv1.repository.home.HomeRepository
import com.example.quizletappandroidv1.repository.studyset.DocumentRepository
import com.example.quizletappandroidv1.repository.user.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    //Data local
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): MyAppDatabase {
        return MyAppDatabase.getInstance(appContext)
    }

    @Provides
    fun provideQuoteDao(database: MyAppDatabase): QuoteDao {
        return database.quoteDao()
    }



}