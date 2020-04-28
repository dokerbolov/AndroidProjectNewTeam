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


    @GET("authentication/token/new")
    fun getRequestToken(@Query("api_key") apiKey: String): Call<RequestToken>

    @GET("authentication/token/new")
    suspend fun getRequestTokenCorountine(@Query("api_key")apiKey: String):Response<RequestToken>

    @POST("authentication/token/validate_with_login")
    fun login(@Query("api_key") apiKey: String, @Body body: JsonObject): Call<JsonObject>

    @POST("authentication/token/validate_with_login")
    suspend fun loginCoroutune(@Query("api_key") apiKey: String,@Body body: JsonObject): Response<JsonObject>


    @POST("authentication/session/new")
    fun getSession(@Query("api_key") apiKey: String, @Body body: JsonObject): Call<JsonObject>

    @POST("authentication/session/new")
    suspend fun getSessionCoroutine(@Query("api_key") apiKey: String,@Body body: JsonObject):Response<JsonObject>

    @GET("account")
    fun getAccount(@Query("api_key") apiKey: String, @Query("session_id") sessionId: String): Call<JsonObject>

    @GET("account")
    suspend fun getAccountCoroutine(@Query("api_key")apiKey: String,@Query("session_id") sessionId: String): Response<JsonObject>

    @POST("account/{account_id}/favorite")
    fun rate(
        @Path("account_id") accountId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
        @Body body: JsonObject
    ): Call<JsonObject>

    @POST("account/{account_id}/favorite")
    suspend fun rateCoroutine(
        @Path("account_id") accountId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
        @Body body: JsonObject
    ):Response<JsonObject>

    @POST("account/{account_id}/favorite")
    fun unrate(
        @Path("account_id") accountId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
        @Body body: JsonObject
    ): Call<JsonObject>

    @GET("account/{account_id}/favorite/movies")
    fun getFavoriteMovies(
        @Path("account_id") accountId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?
    ): Call<MovieResponse>
    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavouriteMoviesCoroutine(
        @Path("account_id") accountId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?
    ): Response<MovieResponse>

    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavoriteMoviesCoroutine(
        @Path("account_id") accountId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?
    ):Response<MovieResponse>

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

    @DELETE("authentication/session")
    fun deleteSession(@Query("api_key") apiKey: String, @Body body: JsonObject): Call<JsonObject>

    @DELETE("authentication/session")
    suspend fun  deleteSessionCoroutine(@Query("api_key") apiKey: String, @Body body: JsonObject): Response<JsonObject>



}