    package com.example.quizletappandroidv1.di

    import com.example.quizletappandroidv1.database.api.retrofit.ApiService
    import com.example.quizletappandroidv1.database.api.retrofit.AuthInterceptor
    import com.example.quizletappandroidv1.utils.Constants
    import dagger.Module
    import dagger.Provides
    import dagger.hilt.InstallIn
    import dagger.hilt.components.SingletonComponent
    import okhttp3.Credentials
    import okhttp3.OkHttpClient
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import javax.inject.Singleton

    @Module
    @InstallIn(SingletonComponent::class)
    object NetworkModule {
        private const val baseUrl = Constants.baseUrl
        private val credentials = Credentials.basic("11167378", "60-dayfreetrial")

        @Provides
        @Singleton
        fun provideRetrofit(): Retrofit {
            return Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder().addInterceptor(AuthInterceptor(credentials)).build()
                )
                .build()
        }

        @Provides
        fun provideApiService(retrofit: Retrofit): ApiService {
            return retrofit.create(ApiService::class.java)
        }
    }