package com.example.moviesearchapp.model

import com.squareup.moshi.Json

data class MovieResponse(
    val page: Int,
    val results: List<Movie>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
)

data class Movie(
    val id: Int,
    val title: String,
    @Json(name = "poster_path") val posterPath: String?,
    val overview: String,
    @Json(name = "release_date") val releaseDate: String?
) {
    val posterUrl: String?
        get() = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
}
