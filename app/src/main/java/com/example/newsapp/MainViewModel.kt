package com.example.newsapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsapp.api.NetworkService
import com.example.newsapp.api.NewsApi
import com.example.newsapp.models.Favourite
import com.example.newsapp.models.News
import kotlinx.coroutines.*
import java.util.*


class MainViewModel : ViewModel() {
    val coroutineScope = CoroutineScope(Dispatchers.Default)
    val countries = mutableMapOf<String, String>()
    val countryId = arrayListOf<Int>()
    private val repository = NewsRepository.getRepo()
    val news = MutableLiveData<List<News>>()
    val favourites = MutableLiveData<List<Favourite>>()

    init {
        getAllCountries()
    }

    fun getNews(query: String, category: String, country: String) {
        coroutineScope.launch {
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
                val res =
                    NetworkService.retrofitService.create(NewsApi::class.java)
                        .getNews(query, category, country)
                res.articles.map {
                    it.category = category
                    it.id = UUID.randomUUID().toString()
                }
                if (query.isBlank()) {
                    withContext(Dispatchers.IO) {
                        res.articles.map {
                            repository.addNews(it)
                        }
                    }
                    getNews(query, category, country)
                } else {
                    news.postValue(res.articles)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
    }

    fun getFavourites() {
        coroutineScope.launch {
            favourites.postValue(repository.getFavourites())
        }
    }

    fun addFavourite(news: News) {
        coroutineScope.launch {
            repository.addFavourite(news)
        }
    }

    fun removeFavourite(favourite: Favourite) {
        coroutineScope.launch {
            repository.removeFavourite(favourite)
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }
}