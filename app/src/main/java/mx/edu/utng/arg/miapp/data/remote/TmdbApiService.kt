package mx.edu.utng.arg.miapp.data.remote

import mx.edu.utng.arg.miapp.data.model.GenreResponse
import mx.edu.utng.arg.miapp.data.model.Movie
import mx.edu.utng.arg.miapp.data.model.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): Response<MovieResponse>

    @GET("movie/{id}")
    suspend fun getMovieDetails(
        @Path("id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<Movie>

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): Response<MovieResponse>

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES"
    ): Response<GenreResponse>

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): Response<MovieResponse>

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): Response<MovieResponse>
}