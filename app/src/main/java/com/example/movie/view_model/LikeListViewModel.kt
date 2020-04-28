package com.example.movie.view_model

import android.content.Context
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

class LikeListViewModel(private val context: Context) : ViewModel(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private var movieDao: MovieDao
    private var sessionId = Singleton.getSession()
    private var accountId = Singleton.getAccountId()
    val liveDataLike = MutableLiveData<State>()

    init {
        movieDao = MovieDatabase.getDatabase(context = context).movieDao()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getMovieLike() {
        liveDataLike.value = State.ShowLoading
        launch {
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
                        val unlikeMoviesOffline = movieDao.getMovieOffline(10)
                        for (movie in unlikeMoviesOffline) {
                            movie.liked = 0
                            movieDao.insert(movie)
                        }
                    }
                } catch (e: Exception) {
                }
            }
            val list = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getPostApi().getFavouriteMoviesCoroutine(
                        accountId,
                        BuildConfig.THE_MOVIE_DB_API_TOKEN,
                        sessionId
                    )
                    if (response.isSuccessful) {
                        val result = response.body()?.results
                        if (result != null) {
                            for (m in result) {
                                m.liked = 1
                            }
                        }
                        if (!result.isNullOrEmpty()) {
                            movieDao.insertAll(result)
                        }
                        result
                    } else {
                        movieDao.getAllLiked()
                    }
                } catch (e: Exception) {
                    movieDao.getAllLiked()
                }
            }
            liveDataLike.value = State.HideLoading
            liveDataLike.value = State.Result(list)

        }
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val list: List<Movie>?) : State()
    }

}