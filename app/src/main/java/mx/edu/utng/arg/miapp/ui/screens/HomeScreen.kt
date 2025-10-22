package mx.edu.utng.arg.miapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.edu.utng.arg.miapp.data.repository.NetworkResult
import mx.edu.utng.arg.miapp.ui.components.LoadingScreen
import mx.edu.utng.arg.miapp.ui.components.MovieCard
import mx.edu.utng.arg.miapp.ui.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MovieViewModel,
    onMovieClick: (Int) -> Unit,
    onFavoriteClick: (Int, Boolean) -> Unit
) {
    val popularMovies by viewModel.popularMovies.collectAsState()
    val nowPlayingMovies by viewModel.nowPlayingMovies.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Movie Explorer",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, "Actualizar")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección: En Cines
            item {
                Text(
                    text = "En Cines",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                when (nowPlayingMovies) {
                    is NetworkResult.Loading -> {
                        LoadingScreen(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                    is NetworkResult.Success -> {
                        val movies = (nowPlayingMovies as NetworkResult.Success).data?.results ?: emptyList()
                        if (movies.isNotEmpty()) {
                            HorizontalMovieList(
                                movies = movies,
                                onMovieClick = onMovieClick,
                                onFavoriteClick = onFavoriteClick
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        // Manejar error
                    }
                }
            }

            // Sección: Populares
            item {
                Text(
                    text = "Populares",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            when (popularMovies) {
                is NetworkResult.Loading -> {
                    item {
                        LoadingScreen(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
                is NetworkResult.Success -> {
                    val movies = (popularMovies as NetworkResult.Success).data?.results ?: emptyList()
                    items(movies.chunked(2)) { rowMovies ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowMovies.forEach { movie ->
                                Box(modifier = Modifier.weight(1f)) {
                                    MovieCard(
                                        movie = movie,
                                        onFavoriteClick = { isFavorite ->
                                            onFavoriteClick(movie.id, isFavorite)
                                        },
                                        onClick = { onMovieClick(movie.id) }
                                    )
                                }
                            }
                            // Agregar espaciador invisible si solo hay una película en la fila
                            if (rowMovies.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                is NetworkResult.Error -> {
                    item {
                        // Manejar error
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalMovieList(
    movies: List<mx.edu.utng.arg.miapp.data.model.Movie>,
    onMovieClick: (Int) -> Unit,
    onFavoriteClick: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(movies) { movie ->
            MovieCard(
                movie = movie,
                onFavoriteClick = { isFavorite ->
                    onFavoriteClick(movie.id, isFavorite)
                },
                onClick = { onMovieClick(movie.id) }
            )
        }
    }
}