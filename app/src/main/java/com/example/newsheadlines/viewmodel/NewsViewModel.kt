package com.example.newsheadlines.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsheadlines.service.Article
import com.example.newsheadlines.service.RetrofitInstance
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val _newsHeadlines = mutableStateOf<List<Article>>(emptyList())
    val newsHeadlines: State<List<Article>> = _newsHeadlines

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        fetchNews()
    }

    fun fetchNews(country: String = "us", category: String = "general") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getTopHeadlines(
                    country = country,
                    category = category,
                    apiKey = "9bac9a3958ac4362b963f0758449e03c"
                )
                if (response.isSuccessful) {
                    val rawResponse = response.body()
                    _newsHeadlines.value = rawResponse?.articles ?: emptyList()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Log.e(
                        "NewsViewModel",
                        "Error fetching news: Code ${response.code()}, $errorMessage"
                    )
                }
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Exception: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

