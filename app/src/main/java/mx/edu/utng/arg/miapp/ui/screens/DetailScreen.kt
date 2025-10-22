package mx.edu.utng.arg.miapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import mx.edu.utng.arg.miapp.data.repository.NetworkResult
import mx.edu.utng.arg.miapp.ui.components.AsyncImage
import mx.edu.utng.arg.miapp.ui.components.LoadingScreen
import mx.edu.utng.arg.miapp.ui.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    movieId: Int,
    viewModel: MovieViewModel,
    onBackClick: () -> Unit
) {
    val movieDetails by viewModel.movieDetails.collectAsState()
    var isFavorite by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(movieId) {
        viewModel.loadMovieDetails(movieId)
        coroutineScope.launch {
            isFavorite = viewModel.isFavorite(movieId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isFavorite = !isFavorite
                        if (isFavorite) {
                            (movieDetails as? NetworkResult.Success)?.data?.let { movie ->
                                viewModel.addToFavorites(movie)
                            }
                        } else {
                            viewModel.removeFromFavorites(movieId)
                        }
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (movieDetails) {
            is NetworkResult.Loading -> {
                LoadingScreen()
            }
            is NetworkResult.Success -> {
                val movie = (movieDetails as NetworkResult.Success).data
                if (movie != null) {
                    MovieDetailContent(
                        movie = movie,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
            is NetworkResult.Error -> {
                // Manejar error
            }
        }
    }
}

@Composable
fun MovieDetailContent(
    movie: mx.edu.utng.arg.miapp.data.model.Movie,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Poster de la película
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            val imageUrl = if (movie.backdropPath != null) {
                "https://image.tmdb.org/t/p/w780${movie.backdropPath}"
            } else if (movie.posterPath != null) {
                "https://image.tmdb.org/t/p/w500${movie.posterPath}"
            } else {
                "https://via.placeholder.com/780x400/CCCCCC/666666?text=No+Image"
            }

            AsyncImage(
                model = imageUrl,
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradiente inferior
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
            ) {
                // Información sobre la imagen
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${movie.releaseDate} • ⭐ ${"%.1f".format(movie.voteAverage)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        // Detalles
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Sinopsis
            Text(
                text = "Sinopsis",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = movie.overview.ifEmpty { "No hay sinopsis disponible." },
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Información adicional
            Text(
                text = "Información",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            InfoRow("Título original", movie.originalTitle)
            InfoRow("Idioma original", movie.originalLanguage.uppercase())
            InfoRow("Fecha de estreno", movie.releaseDate)
            InfoRow("Calificación", "⭐ ${"%.1f".format(movie.voteAverage)} (${movie.voteCount} votos)")
            InfoRow("Popularidad", "%.0f".format(movie.popularity))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}