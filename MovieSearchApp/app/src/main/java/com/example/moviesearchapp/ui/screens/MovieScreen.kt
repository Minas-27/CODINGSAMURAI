package com.example.moviesearchapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.moviesearchapp.R
import com.example.moviesearchapp.model.Movie

@Composable
fun MovieSearchScreen(
    movies: List<Movie>,
    onSearchClicked: (String) -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var selectedMovie by remember { mutableStateOf<Movie?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search Movies", color = Color.White.copy(alpha = 0.6f)) },
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onSearchClicked(query) },
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
        ) {
            Text("Search", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedMovie == null) {
            MovieList(movies) { movie ->
                selectedMovie = movie
            }
        } else {
            MovieDetailScreen(selectedMovie!!) {
                selectedMovie = null
            }
        }
    }
}

@Composable
fun MovieList(movies: List<Movie>, onItemClick: (Movie) -> Unit) {
    LazyColumn {
        items(movies) { movie ->
            MovieItem(movie) {
                onItemClick(movie)
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFF1A1A1A), RoundedCornerShape(8.dp))
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        if (movie.posterUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(movie.posterUrl),
                contentDescription = movie.title,
                modifier = Modifier.size(100.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(movie.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(movie.releaseDate ?: "Unknown", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(movie.overview, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall, color = Color.White)
        }
    }
}

@Composable
fun MovieDetailScreen(movie: Movie, onBack: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        IconButton(onClick = onBack) {
            Image(
                painter = painterResource(id = R.drawable.outline_arrow_back_24),
                contentDescription = "Back"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (movie.posterUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(movie.posterUrl),
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(movie.title, style = MaterialTheme.typography.titleLarge, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Text(movie.releaseDate ?: "Unknown", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            movie.overview,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0D0D)
@Composable
fun MovieSearchScreenPreview() {
    val sampleMovies = listOf(
        Movie(
            id = 1,
            title = "Spider-Man: No Way Home",
            posterPath = "/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg",
            overview = "Peter Parker is unmasked and no longer able to separate his normal life from the high-stakes of being a super-hero.",
            releaseDate = "2021-12-15"
        ),
        Movie(
            id = 2,
            title = "The Batman",
            posterPath = "/74xTEgt7R36Fpooo50r9T25onhq.jpg",
            overview = "Batman ventures into Gotham City's underworld when a sadistic killer leaves behind a trail of cryptic clues.",
            releaseDate = "2022-03-01"
        )
    )

    MovieSearchScreen(movies = sampleMovies)
}
