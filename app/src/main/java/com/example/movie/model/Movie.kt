package com.example.movie.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "movies_table")
data class Movie(
    @PrimaryKey
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("popularity")
    val populatiry: Double? = null,
    @SerializedName("vote_count")
    val vote_count: Int? = null,
    @SerializedName("video")
    val video: Boolean? = null,
    @SerializedName("poster_path")
    val poster_path: String? = null,
    @SerializedName("adult")
    val adult: Boolean? = null,
    @SerializedName("backdrop_path")
    val backdrop_path: String? = null,
    @SerializedName("original_language")
    val original_language: String? = null,
    @SerializedName("original_title")
    val original_title: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("vote_average")
    val vote_average: Double? = null,
    @SerializedName("overview")
    val overview: String? = null,
    @SerializedName("release_date")
    val release_date: String? = null,
    @SerializedName("liked")
    var liked: Int? = 0
) : Serializable {

    fun getPosterPath(): String {
        return "https://image.tmdb.org/t/p/w500$poster_path"
    }
}