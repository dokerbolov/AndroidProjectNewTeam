package com.example.movie.api

import com.example.movie.model.Movie
import com.example.movie.model.MovieResponse
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object RetrofitService {

    const val BASE_URL = "https://api.themoviedb.org/3/"

    fun getPostApi(): PostApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(PostApi::class.java)
    }
}

interface PostApi {

    @GET("movie/popular")
    fun getPopularMovieList(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("movie/popular")
    suspend fun getPopularMovieListCoroutine(@Query("api_key") apiKey: String): Response<MovieResponse>


    @GET("movie/{movie_id}/account_states")
    fun hasLike(
        @Path("movie_id") movieId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?
    ): Call<JsonObject>

    @GET("movie/{movie_id}/account_states")
    suspend fun hasLikeCoroutine(
        @Path("movie_id") movieId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?
        ):Response<JsonObject>



}