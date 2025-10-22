package mx.edu.utng.arg.miapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.edu.utng.arg.miapp.data.local.database.AppDatabase
import mx.edu.utng.arg.miapp.data.repository.MovieRepository
import mx.edu.utng.arg.miapp.ui.screens.DetailScreen
import mx.edu.utng.arg.miapp.ui.screens.FavoritesScreen
import mx.edu.utng.arg.miapp.ui.screens.HomeScreen
import mx.edu.utng.arg.miapp.ui.screens.SearchScreen
import mx.edu.utng.arg.miapp.ui.theme.MovieExplorerTheme
import mx.edu.utng.arg.miapp.ui.viewmodel.MovieViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieExplorerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MovieExplorerApp()
                }
            }
        }
    }
}

/**
 * Composable que crea y recuerda el ViewModelFactory
 * configurado con todas las dependencias necesarias
 */
@Composable
fun rememberViewModelFactory(): ViewModelProvider.Factory {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val repository = remember { MovieRepository(database.favoriteDao()) }

    return remember {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
                    return MovieViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}

@Composable
fun MovieExplorerApp() {
    val navController = rememberNavController()
    val factory = rememberViewModelFactory()
    val viewModel: MovieViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onMovieClick = { movieId ->
                    navController.navigate("detail/$movieId")
                },
                onFavoriteClick = { movieId, isFavorite ->
                    if (!isFavorite) {
                        viewModel.removeFromFavorites(movieId)
                    }
                }
            )
        }

        composable("search") {
            SearchScreen(
                viewModel = viewModel,
                onMovieClick = { movieId ->
                    navController.navigate("detail/$movieId")
                },
                onFavoriteClick = { movieId, isFavorite ->
                    if (!isFavorite) {
                        viewModel.removeFromFavorites(movieId)
                    }
                }
            )
        }

        composable("favorites") {
            FavoritesScreen(
                viewModel = viewModel,
                onMovieClick = { movieId ->
                    navController.navigate("detail/$movieId")
                },
                onFavoriteClick = { movieId, isFavorite ->
                    if (!isFavorite) {
                        viewModel.removeFromFavorites(movieId)
                    }
                }
            )
        }

        composable("detail/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: -1
            DetailScreen(
                movieId = movieId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}