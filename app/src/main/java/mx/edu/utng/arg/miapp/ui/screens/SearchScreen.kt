package mx.edu.utng.arg.miapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.edu.utng.arg.miapp.data.repository.NetworkResult
import mx.edu.utng.arg.miapp.ui.components.LoadingScreen
import mx.edu.utng.arg.miapp.ui.components.MovieCard
import mx.edu.utng.arg.miapp.ui.components.SearchBar
import mx.edu.utng.arg.miapp.ui.viewmodel.MovieViewModel

@Composable
fun SearchScreen(
    viewModel: MovieViewModel,
    onMovieClick: (Int) -> Unit,
    onFavoriteClick: (Int, Boolean) -> Unit
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.searchMovies(it) }
        )

        when (searchResults) {
            is NetworkResult.Loading -> {
                if (searchQuery.isNotEmpty()) {
                    LoadingScreen()
                }
            }
            is NetworkResult.Success -> {
                val movies = (searchResults as NetworkResult.Success).data?.results ?: emptyList()
                if (movies.isNotEmpty()) {
                    Text(
                        text = "Resultados (${movies.size})",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(movies.chunked(2)) { rowMovies ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                } else if (searchQuery.isNotEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron resultados para \"$searchQuery\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            is NetworkResult.Error -> {
                // Manejar error
            }
        }
    }
}