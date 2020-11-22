package com.example.newsapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsapp.api.NetworkService
import com.example.newsapp.api.NewsApi
import com.example.newsapp.models.News
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*

private const val TAG = "MainViewModel"

class MainViewModel : ViewModel() {
    val coroutineScope = CoroutineScope(Dispatchers.Default)
    val countries = mutableMapOf<String, String>()
    val countryId = arrayListOf<Int>()
    private val repository = NewsRepository.getRepo()
    val news = MutableLiveData<List<News>>()

    init {
        getAllCountries()
    }

    fun getNews(query: String, category: String, country: String) {
        coroutineScope.launch {
            Log.d(TAG, "getNews: $query")
            val localNews = repository.getNews(category)
            if (localNews.isEmpty() || query.isNotBlank()) {
                getNewsOnline(query, category, country)
            } else {
                news.postValue(localNews)
            }
        }
    }

    private fun getNewsOnline(query: String, category: String, country: String) {
        try {
            coroutineScope.launch {
                Log.d(TAG, "getNewsOnline MakingRequests: ")
                val res =
                    NetworkService.retrofitService.create(NewsApi::class.java)
                        .getNews(query, category, country)
                Log.d(TAG, "getNewsOnline MakingRequests: ${res.status}")
                res.articles.map {
                    it.category = category
                    it.id = UUID.randomUUID().toString()
                }
                if (query.isBlank()) {
                    Log.d(TAG, "Empty Query: $query")
                    withContext(Dispatchers.IO) {
                        res.articles.map {
                            repository.addNews(it)
                        }
                    }
                    getNews(query, category, country)
                } else {
                    Log.d(TAG, "New Query: $query")
                    news.postValue(res.articles)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "getNews: Exception")
        }
    }

    private fun getAllCountries() {
        for (code in Utils.countryCodes) {
            val codeUpper = code.toUpperCase(Locale.ROOT)
            val name = Locale("", codeUpper).displayName
            val hashCode = Locale("", codeUpper).hashCode()
            countries[codeUpper] = name
            countryId.add(hashCode)
        }
        Log.d(TAG, "getAllCountries: ${countries.size}")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
        coroutineScope.cancel()
    }
}