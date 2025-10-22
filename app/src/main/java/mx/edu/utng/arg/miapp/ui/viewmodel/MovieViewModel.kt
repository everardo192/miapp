package mx.edu.utng.arg.miapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utng.arg.miapp.data.model.Movie
import mx.edu.utng.arg.miapp.data.model.MovieResponse
import mx.edu.utng.arg.miapp.data.repository.MovieRepository
import mx.edu.utng.arg.miapp.data.repository.NetworkResult

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    // Estado para películas populares
    private val _popularMovies = MutableStateFlow<NetworkResult<MovieResponse>>(NetworkResult.Loading())
    val popularMovies: StateFlow<NetworkResult<MovieResponse>> = _popularMovies.asStateFlow()

    // Estado para búsqueda
    private val _searchResults = MutableStateFlow<NetworkResult<MovieResponse>>(NetworkResult.Loading())
    val searchResults: StateFlow<NetworkResult<MovieResponse>> = _searchResults.asStateFlow()

    // Estado para detalles de película
    private val _movieDetails = MutableStateFlow<NetworkResult<Movie>>(NetworkResult.Loading())
    val movieDetails: StateFlow<NetworkResult<Movie>> = _movieDetails.asStateFlow()

    // Estado para favoritos
    private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
    val favorites: StateFlow<List<Movie>> = _favorites.asStateFlow()

    // Estado para películas en tendencia
    private val _nowPlayingMovies = MutableStateFlow<NetworkResult<MovieResponse>>(NetworkResult.Loading())
    val nowPlayingMovies: StateFlow<NetworkResult<MovieResponse>> = _nowPlayingMovies.asStateFlow()

    // Query de búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadPopularMovies()
        loadNowPlayingMovies()
        loadFavorites()
    }

    fun loadPopularMovies(page: Int = 1) {
        viewModelScope.launch {
            repository.getPopularMovies(page).collect { result ->
                _popularMovies.value = result
            }
        }
    }

    fun loadNowPlayingMovies(page: Int = 1) {
        viewModelScope.launch {
            repository.getNowPlayingMovies(page).collect { result ->
                _nowPlayingMovies.value = result
            }
        }
    }

    fun searchMovies(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = NetworkResult.Success(MovieResponse(0, emptyList(), 0, 0))
            return
        }

        viewModelScope.launch {
            repository.searchMovies(query).collect { result ->
                _searchResults.value = result
            }
        }
    }

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            repository.getMovieDetails(movieId).collect { result ->
                _movieDetails.value = result
            }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavorites().collect { movies ->
                _favorites.value = movies
            }
        }
    }

    fun addToFavorites(movie: Movie) {
        viewModelScope.launch {
            repository.addToFavorites(movie)
            loadFavorites()
        }
    }

    fun removeFromFavorites(movieId: Int) {
        viewModelScope.launch {
            repository.removeFromFavorites(movieId)
            loadFavorites()
        }
    }

    suspend fun isFavorite(movieId: Int): Boolean {
        return repository.isFavorite(movieId)
    }

    fun refresh() {
        loadPopularMovies()
        loadNowPlayingMovies()
        loadFavorites()
    }
}