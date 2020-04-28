package com.example.movie.view_model

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie.BuildConfig
import com.example.movie.api.RetrofitService
import com.example.movie.database.MovieDao
import com.example.movie.database.MovieDatabase
import com.example.movie.model.FavResponse
import com.example.movie.model.Movie
import com.example.movie.model.Singleton
import com.google.gson.Gson
import com.example.movie.R
import com.google.gson.JsonObject
import kotlinx.coroutines.*

import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class DetailViewModel(private val context: Context) : ViewModel(), CoroutineScope {
    private var movieDao: MovieDao? = null
    val liveData = MutableLiveData<State>()
    private val sessionId = Singleton.getSession()
    private val accountId = Singleton.getAccountId()
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    init {
        movieDao = MovieDatabase.getDatabase(context = context).movieDao()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun haslike(movieId: Int?) {
        liveData.value = State.ShowLoading
        launch {
            val likeInt = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getPostApi()
                        .hasLikeCoroutine(
                            movieId,
                            BuildConfig.THE_MOVIE_DB_API_TOKEN, sessionId
                        )
                    Log.d("TAG", response.toString())
                    if (response.isSuccessful) {
                        val gson = Gson()
                        val like = gson.fromJson(
                            response.body(),
                            FavResponse::class.java
                        ).favorite
                        if (like)
                            1
                        else 0
                    } else {
                        movieDao?.getLiked(movieId) ?: 0
                    }
                } catch (e: Exception) {
                    movieDao?.getLiked(movieId) ?: 0
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(likeInt)
        }
    }

    fun likeMovie(favourite: Boolean, movie: Movie?, movieId: Int?) {
        liveData.value = State.ShowLoading
        launch {
            val body = JsonObject().apply {
                addProperty("media_type", "movie")
                addProperty("media_id", movieId)
                addProperty("favorite", favourite)
            }
            try {
                RetrofitService.getPostApi()
                    .rateCoroutine(
                        accountId,
                        BuildConfig.THE_MOVIE_DB_API_TOKEN, sessionId, body
                    )
            } catch (e: Exception) {
            }
            if (favourite) {
                movie?.liked = 11
                movieDao?.insert(movie)
                Toast.makeText(
                    context,
                    context.getString(R.string.fav_add),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                movie?.liked = 10
                movieDao?.insert(movie)
                Toast.makeText(
                    context,
                    context.getString(R.string.fav_remove),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        liveData.value = State.HideLoading
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val likeInt: Int?) : State()
    }
}
