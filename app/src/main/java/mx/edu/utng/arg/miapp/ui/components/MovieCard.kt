package mx.edu.utng.arg.miapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import mx.edu.utng.arg.miapp.data.model.Movie

@Composable
fun MovieCard(
    movie: Movie,
    isFavorite: Boolean = false,
    onFavoriteClick: (Boolean) -> Unit = {},
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            ) {
                val imageUrl = if (movie.posterPath != null) {
                    "https://image.tmdb.org/t/p/w500${movie.posterPath}"
                } else {
                    "https://via.placeholder.com/150x200/CCCCCC/666666?text=No+Image"
                }

                AsyncImage(
                    model = imageUrl,
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                // Botón de favoritos
                IconButton(
                    onClick = { onFavoriteClick(!isFavorite) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Rating
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                    )
                ) {
                    Text(
                        text = "%.1f".format(movie.voteAverage),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Información de la película
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = movie.releaseDate.take(4), // Solo el año
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AsyncImage(
    model: String,
    contentDescription: String,
    modifier: Modifier,
    contentScale: ContentScale
) {
    TODO("Not yet implemented")
}