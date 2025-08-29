package com.example.moviesearchapp.repository

import com.example.moviesearchapp.model.MovieResponse
import com.example.moviesearchapp.network.RetrofitInstance

class MovieRepository {
    suspend fun searchMovies(query: String, apiKey: String): MovieResponse {
        return RetrofitInstance.api.searchMovies(apiKey, query)
    }
}
