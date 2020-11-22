package com.example.newsapp.api

import com.example.newsapp.Utils
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkService {
    private val requestInterceptor = Interceptor { chain ->
        val url =
            chain.request()
                .url()
                .newBuilder()
                .addQueryParameter("apiKey", Utils.API_KEY)
                .build()
        val request = chain.request()
            .newBuilder()
            .url(url)
            .build()

        return@Interceptor chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(requestInterceptor)
        .build()

    val retrofitService: Retrofit = Retrofit
        .Builder()
        .client(okHttpClient)
        .baseUrl(Utils.BASE_URL)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}