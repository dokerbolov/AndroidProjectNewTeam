package com.example.movie.myFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.movie.BuildConfig
import com.example.movie.R
import com.example.movie.adapter.LikeMoviesAdapter
import com.example.movie.api.RetrofitService
import com.example.movie.database.MovieDatabase
import com.example.movie.database.MovieDao
import com.example.movie.model.Movie
import com.example.movie.model.Singleton
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class LikeFragment : Fragment(), CoroutineScope {
    private lateinit var relativeLayout: RelativeLayout
    private lateinit var commentsIc: ImageView
    private lateinit var timeIc: ImageView
    private lateinit var recyclerView: RecyclerView
    private var dateTv: TextView? = null
    private var commentsTv: TextView? = null
    private var bigPictv: TextView? = null
    private var bigPicCardIm: ImageView? = null
    private var postAdapter: LikeMoviesAdapter? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var movieList: List<Movie>
    lateinit var movie: Movie
    private var rootView: View? = null
    private var sessionId = Singleton.getSession()
    private var accountId = Singleton.getAccountId()
    private var movieDao: MovieDao? = null
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        movieDao = MovieDatabase.getDatabase(context!!).movieDao()
        rootView = inflater.inflate(R.layout.activity_main, container, false) as ViewGroup
        bindView()
        relativeLayout.visibility = View.INVISIBLE
        relativeLayout.visibility = View.GONE
        swipeRefreshLayout.setOnRefreshListener {
            initViews()
        }
        initViews()
        return rootView
    }

    private fun initViews() {
        bigPicCardIm?.visibility = View.INVISIBLE
        movieList = ArrayList()
        postAdapter = activity?.applicationContext?.let { LikeMoviesAdapter(it, movieList) }!!
        recyclerView.layoutManager = GridLayoutManager(activity, 1)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = postAdapter
        postAdapter?.notifyDataSetChanged()
        loadJSON()
    }

    private fun loadJSON() {
        getMovieLikesCoroutine()
    }

    private fun bindView() {
        commentsIc = (rootView as ViewGroup).findViewById(R.id.ic_comments)
        timeIc = (rootView as ViewGroup).findViewById(R.id.ic_times)
        dateTv = (rootView as ViewGroup).findViewById(R.id.date_movie_info)
        commentsTv = (rootView as ViewGroup).findViewById(R.id.comment_movie_info)
        bigPicCardIm = (rootView as ViewGroup).findViewById(R.id.main_big_pic)
        bigPictv = (rootView as ViewGroup).findViewById(R.id.main_big_tv)
        recyclerView = (rootView as ViewGroup).findViewById(R.id.recycler_view)
        relativeLayout = (rootView as ViewGroup).findViewById(R.id.main_layout_pic)
        swipeRefreshLayout = (rootView as ViewGroup).findViewById(R.id.main_content)
    }

    private fun getMovieLikesCoroutine() {
        launch {
            swipeRefreshLayout.isRefreshing = true
            val likesOffline = movieDao?.getLikedOffline(11)
            if (likesOffline != null) {
                for (i in likesOffline) {
                    val body = JsonObject().apply {
                        addProperty("media_type", "movie")
                        addProperty("media_id", i)
                        addProperty("favorite", true)
                    }
                    try {
                        RetrofitService.getPostApi()
                            .rateCoroutine(
                                accountId,
                                BuildConfig.THE_MOVIE_DB_API_TOKEN,
                                sessionId,
                                body
                            )
                    } catch (e: Exception) {

                    }
                }
            }

            val unLikesOffline = movieDao?.getLikedOffline(10)
            if (unLikesOffline != null) {
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
                    } catch (e: Exception) {
                    }
                }
            }

            val unLikeMoviesOffline = movieDao?.getUnLikedOffline()
            val newArray: ArrayList<Movie>? = null
            if (unLikeMoviesOffline != null) {
                for (movie in unLikeMoviesOffline) {
                    movie.liked = 0
                    newArray?.add(movie)
                }
            }
            if (movieDao != null) newArray?.let { movieDao?.insertAll(it) }

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
                            movieDao?.insertAll(result)
                        }
                        result
                    } else {
                        movieDao?.getAllLiked() ?: emptyList()
                    }
                } catch (e: Exception) {
                    movieDao?.getAllLiked() ?: emptyList()
                }
            }
            postAdapter?.moviesList = list
            postAdapter?.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}






