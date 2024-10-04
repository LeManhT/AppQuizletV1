package com.example.quizletappandroidv1.di

import android.content.Context
import com.example.quizletappandroidv1.dao.FavouriteDao
import com.example.quizletappandroidv1.dao.QuoteDao
import com.example.quizletappandroidv1.database.api.retrofit.ApiService
import com.example.quizletappandroidv1.database.api.retrofit.QuoteApiService
import com.example.quizletappandroidv1.database.local.MyAppDatabase
import com.example.quizletappandroidv1.repository.home.HomeRepository
import com.example.quizletappandroidv1.repository.quote.QuoteRepository
import com.example.quizletappandroidv1.repository.studyset.DocumentRepository
import com.example.quizletappandroidv1.repository.user.UserRepository
import com.example.quizletappandroidv1.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val baseUrl = Constants.baseUrl
    private val credentials = Credentials.basic("11167378", "60-dayfreetrial")
    private const val baseQuoteUrl = Constants.baseUrlQuote

    @Provides
    @Singleton
    @Named("mainRetrofit")
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
    }

    @Provides
    @Singleton
    @Named("quoteRetrofit")
    fun provideQuoteRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(baseQuoteUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
    }

    @Provides
    fun provideApiService(@Named("mainRetrofit") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    fun provideApiQuoteService(@Named("quoteRetrofit") retrofit: Retrofit): QuoteApiService {
        return retrofit.create(QuoteApiService::class.java)
    }

    @Provides
    fun provideUserRepository(apiService: ApiService): UserRepository {
        return UserRepository(apiService)
    }

    @Provides
    fun provideHomeRepository(apiService: ApiService): HomeRepository {
        return HomeRepository(apiService)
    }

    @Provides
    fun provideDocumentRepository(apiService: ApiService): DocumentRepository {
        return DocumentRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideMyAppDatabase(@ApplicationContext context: Context): MyAppDatabase {
        return MyAppDatabase.getInstance(context)
    }

    @Provides
    fun provideQuoteRepository(
        quoteApiService: QuoteApiService,
        quoteDb: MyAppDatabase,
        @ApplicationContext context: Context
    ): QuoteRepository {
        return QuoteRepository(quoteApiService, quoteDb, context)
    }


    @Provides
    fun provideQuoteDao(database: MyAppDatabase): QuoteDao {
        return database.quoteDao()
    }

    @Provides
    fun provideFavouriteNewWordDao(database: MyAppDatabase): FavouriteDao {
        return database.favouriteNewWordDao()
    }

}