package com.example.quizletappandroidv1.database.api.retrofit

import com.example.quizletappandroidv1.models.QuoteResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface QuoteApiService {

    @GET("/quotes")
    suspend fun getQuoteFromServer(
        @Query("page") page: Int
    ): Response<QuoteResponse>

    @GET("/quotes/random")
    suspend fun getRandomQuote(): Response<QuoteResponse>
}
