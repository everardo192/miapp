package mx.edu.utng.arg.miapp.data.model

import com.google.gson.annotations.SerializedName

data class Genre(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String
)

data class GenreResponse(
    @SerializedName("genres")
    val genres: List<Genre>
)