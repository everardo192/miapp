package mx.edu.utng.arg.miapp.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import mx.edu.utng.arg.miapp.data.local.dao.FavoriteDao
import mx.edu.utng.arg.miapp.data.local.entity.FavoriteMovie
import mx.edu.utng.arg.miapp.data.model.Movie
import mx.edu.utng.arg.miapp.data.model.MovieResponse
import mx.edu.utng.arg.miapp.data.remote.RetrofitClient
import mx.edu.utng.arg.miapp.data.remote.TmdbApiService

class MovieRepository(private val favoriteDao: FavoriteDao) {

    private val apiService: TmdbApiService by lazy {
        RetrofitClient.createService(TmdbApiService::class.java)
    }

    private val apiKey = RetrofitClient.getApiKey()

    // Obtener películas populares
    fun getPopularMovies(page: Int = 1): Flow<NetworkResult<MovieResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.getPopularMovies(apiKey, page, "es-ES")
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Error desconocido"))
        }
    }.flowOn(Dispatchers.IO)

    // Buscar películas
    fun searchMovies(query: String, page: Int = 1): Flow<NetworkResult<MovieResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.searchMovies(apiKey, query, page, "es-ES")
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.results.isEmpty()) {
                    emit(NetworkResult.Error("No se encontraron películas"))
                } else {
                    emit(NetworkResult.Success(body))
                }
            } else {
                emit(NetworkResult.Error("Error en la búsqueda"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Error de red"))
        }
    }.flowOn(Dispatchers.IO)

    // Obtener detalles de película
    fun getMovieDetails(movieId: Int): Flow<NetworkResult<Movie>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.getMovieDetails(movieId, apiKey)
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Película no encontrada"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Error al cargar película"))
        }
    }.flowOn(Dispatchers.IO)

    // Obtener películas en tendencia
    fun getNowPlayingMovies(page: Int = 1): Flow<NetworkResult<MovieResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = apiService.getNowPlayingMovies(apiKey, page, "es-ES")
            if (response.isSuccessful && response.body() != null) {
                emit(NetworkResult.Success(response.body()!!))
            } else {
                emit(NetworkResult.Error("Error al cargar películas"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Error de red"))
        }
    }.flowOn(Dispatchers.IO)

    // Favoritos - Room
    fun getFavorites(): Flow<List<Movie>> = flow {
        val favorites = favoriteDao.getAllFavorites()
        favorites.collect { favoriteMovies ->
            val movies = favoriteMovies.map { favorite ->
                Movie(
                    id = favorite.id,
                    title = favorite.title,
                    overview = favorite.overview,
                    posterPath = favorite.posterPath,
                    backdropPath = null,
                    releaseDate = favorite.releaseDate,
                    voteAverage = favorite.voteAverage,
                    voteCount = favorite.voteCount,
                    genreIds = emptyList(),
                    adult = false,
                    originalLanguage = "es",
                    originalTitle = favorite.title,
                    popularity = 0.0,
                    video = false
                )
            }
            emit(movies)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun addToFavorites(movie: Movie) {
        val favoriteMovie = FavoriteMovie(
            id = movie.id,
            title = movie.title,
            overview = movie.overview,
            posterPath = movie.posterPath,
            releaseDate = movie.releaseDate,
            voteAverage = movie.voteAverage,
            voteCount = movie.voteCount
        )
        favoriteDao.insertFavorite(favoriteMovie)
    }

    suspend fun removeFromFavorites(movieId: Int) {
        favoriteDao.deleteFavoriteById(movieId)
    }

    suspend fun isFavorite(movieId: Int): Boolean {
        return favoriteDao.getFavoriteById(movieId) != null
    }
}

// NetworkResult sealed class (debe estar en el mismo archivo o en uno separado)
sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String, data: T? = null) : NetworkResult<T>(data, message)
    class Loading<T> : NetworkResult<T>()
}