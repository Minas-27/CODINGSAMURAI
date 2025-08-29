package com.example.moviesearchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.moviesearchapp.ui.screens.MovieSearchScreen
import com.example.moviesearchapp.ui.theme.MovieSearchAppTheme
import com.example.moviesearchapp.viewmodel.MovieViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieSearchAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val viewModel: MovieViewModel = viewModel()
                    val movies = viewModel.movies.collectAsState().value
                    MovieSearchScreen(
                        movies = movies,
                        onSearchClicked = { query ->
                            viewModel.searchMovies(query, "03e03631ee09c5a26f6c8c883e3bfa5b")
                        }
                    )
                }
            }
        }
    }
}
