package mx.edu.utng.arg.miapp.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mx.edu.utng.arg.miapp.data.local.entity.FavoriteMovie

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_movies ORDER BY title ASC")
    fun getAllFavorites(): Flow<List<FavoriteMovie>>

    @Query("SELECT * FROM favorite_movies WHERE id = :movieId")
    suspend fun getFavoriteById(movieId: Int): FavoriteMovie?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(movie: FavoriteMovie)

    @Delete
    suspend fun deleteFavorite(movie: FavoriteMovie)

    @Query("DELETE FROM favorite_movies WHERE id = :movieId")
    suspend fun deleteFavoriteById(movieId: Int)
}