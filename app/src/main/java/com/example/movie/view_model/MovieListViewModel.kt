package com.example.movie.view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie.BuildConfig
import com.example.movie.api.RetrofitService
import com.example.movie.database.MovieDao
import com.example.movie.database.MovieDatabase
import com.example.movie.model.Movie
import com.example.movie.model.Singleton
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MovieListViewModel(
    private val context: Context
) : ViewModel(), CoroutineScope {
    private val job = Job()
    private var movieDao: MovieDao
    private var sessionId = Singleton.getSession()
    private var accountId = Singleton.getAccountId()
    val liveData = MutableLiveData<State>()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    init {
        movieDao = MovieDatabase.getDatabase(context = context).movieDao()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getMoviesList() {
        launch {
            liveData.value = State.ShowLoading
            val likesOffline = movieDao.getIdOffline(11)
            for (i in likesOffline) {
                val body = JsonObject().apply {
                    addProperty("media_type", "movie")
                    addProperty("media_id", i)
                    addProperty("favorite", true)
                }
                try {
                    val response = RetrofitService.getPostApi()
                        .rateCoroutine(
                            accountId,
                            BuildConfig.THE_MOVIE_DB_API_TOKEN,
                            sessionId,
                            body
                        )
                    if (response.isSuccessful) {
                        val likeMoviesOffline = movieDao.getMovieOffline(11)
                        for (movie in likeMoviesOffline) {
                            movie.liked = 1
                            movieDao.insert(movie)
                        }
                    }
                } catch (e: Exception) {
                }
            }

            val unLikesOffline = movieDao.getIdOffline(10)
            for (i in unLikesOffline) {
                val body = JsonObject().apply {
                    addProperty("media_type", "movie")
                    addProperty("media_id", i)
                    addProperty("favorite", false)
                }
                try {
                    val response = RetrofitService.getPostApi()
                        .rateCoroutine(
                            accountId,
                            BuildConfig.THE_MOVIE_DB_API_TOKEN,
                            sessionId,
                            body
                        )
                    if (response.isSuccessful) {
                        val unLikeMoviesOffline = movieDao.getMovieOffline(10)
                        for (movie in unLikeMoviesOffline) {
                            movie.liked = 0
                            movieDao.insert(movie)
                        }
                    }
                } catch (e: Exception) {
                }
            }
            val list = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getPostApi()
                        .getPopularMovieListCoroutine(BuildConfig.THE_MOVIE_DB_API_TOKEN)
                    if (response.isSuccessful) {
                        Log.d("TAG", "")
                        val result = response.body()?.results
                        val result2 = result?.subList(1, result.lastIndex)
                        if (!result2.isNullOrEmpty()) {
                            movieDao.insertAll(result)
                        }
                        result2
                    } else {
                        Log.d("TAG", "")
                        movieDao.getAll()
                    }
                } catch (e: Exception) {
                    Log.d("TAG", "")
                    movieDao.getAll()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list)
        }
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val list: List<Movie>?) : State()
    }
}

