package com.example.moviesearchapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesearchapp.model.Movie
import com.example.moviesearchapp.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    private val repository = MovieRepository()

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    fun searchMovies(query: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = repository.searchMovies(query, apiKey)
                println("Movies found: ${response.results.size}") // debug
                _movies.value = response.results
            } catch (e: Exception) {
                e.printStackTrace()
                _movies.value = emptyList()
            }
        }
    }
}
