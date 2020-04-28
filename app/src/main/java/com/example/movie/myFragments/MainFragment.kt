package com.example.movie.myFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.movie.BuildConfig
import com.example.movie.DetailActivity
import com.example.movie.R
import com.example.movie.adapter.MoviesAdapter
import com.example.movie.api.RetrofitService
import com.example.movie.database.MovieDao
import com.example.movie.database.MovieDatabase
import com.example.movie.model.Movie
import com.example.movie.model.Singleton
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment(), CoroutineScope by MainScope() {
    private var relativeLayout: RelativeLayout? = null
    private lateinit var commentsIc: ImageView
    private lateinit var timeIc: ImageView
    private lateinit var recyclerView: RecyclerView
    private var dateTv: TextView? = null
    private var commentsTv: TextView? = null
    private var bigPictv: TextView? = null
    private var bigPicCardIm: ImageView? = null
    private var postAdapter: MoviesAdapter? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var movieList: List<Movie>
    private lateinit var movie: Movie
    private var rootView: View? = null
    private var movieDao: MovieDao? = null
    private var accountId: Int? = null
    private var sessionId: String? = ""
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        movieDao = MovieDatabase.getDatabase(context!!).movieDao()
        sessionId = Singleton.getSession()
        accountId = Singleton.getAccountId()
        rootView = inflater.inflate(R.layout.activity_main, container, false) as ViewGroup
        bindViews()

        relativeLayout?.setOnClickListener {
            intentFun()
        }

        swipeRefreshLayout.setOnRefreshListener {
            if (swipeRefreshLayout.isRefreshing) {
                commentsIc.visibility = View.INVISIBLE
                timeIc.visibility = View.INVISIBLE
            } else {
                commentsIc.visibility = View.VISIBLE
                timeIc.visibility = View.VISIBLE
            }
            initViews()
        }
        bigPicCard()
        initViews()
        return rootView
    }

    private fun intentFun() {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("movie_id", movie.id)
        intent.putExtra("original_title", movie.original_title)
        intent.putExtra("poster_path", movie.getPosterPath())
        intent.putExtra("overview", movie.overview)
        intent.putExtra("vote_average", (movie.vote_average).toString())
        intent.putExtra("release_date", movie.release_date)
        intent.putExtra("movie", movie)
        view?.context?.startActivity(intent)
    }

    private fun bindViews() {
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

    private fun initViews() {
        commentsIc.visibility = View.INVISIBLE
        timeIc.visibility = View.INVISIBLE
        bigPictv?.text = ""
        dateTv?.text = ""
        commentsTv?.text = ""
        commentsIc.setImageBitmap(null)
        timeIc.setImageBitmap(null)
        bigPicCardIm?.visibility = View.INVISIBLE
        movieList = ArrayList()
        postAdapter = activity?.applicationContext?.let { MoviesAdapter(it, movieList) }!!
        recyclerView.layoutManager = GridLayoutManager(activity, 1)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = postAdapter
        postAdapter?.notifyDataSetChanged()
        bigPicCard()
        loadJSON()
    }

    private fun bigPicCard() {
        getMovieCoroutine()
        commentsIc.visibility = View.VISIBLE
        timeIc.visibility = View.VISIBLE
    }

    private fun loadJSON() {
        try {
            getMovieListCoroutine()
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun getMovieCoroutine() {
        launch {
            swipeRefreshLayout.isRefreshing = true
            val list = withContext(Dispatchers.IO)
            {
                try {
                    val response = RetrofitService.getPostApi()
                        .getPopularMovieListCoroutine(BuildConfig.THE_MOVIE_DB_API_TOKEN)
                    if (response.isSuccessful) {
                        val result = response.body()?.results

                        if (!result.isNullOrEmpty()) {
                            movieDao?.insertAll(result)
                        }
                        result
                    } else {
                        movieDao?.getAll() ?: emptyList()
                    }

                } catch (e: Exception) {
                    movieDao?.getAll() ?: emptyList()
                }
            }
            movie = list!!.first()
            dateTv?.text = "март 30, 2020"
            commentsTv?.text = "0"
            bigPictv?.text = movie.original_title
            bigPicCardIm?.visibility = View.VISIBLE
            Glide.with(rootView!!.context)
                .load(movie.getPosterPath())
                .into((rootView as ViewGroup).findViewById(R.id.main_big_pic))
            commentsIc.visibility = View.VISIBLE
            timeIc.visibility = View.VISIBLE
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun getMovieListCoroutine() {
        launch {
            swipeRefreshLayout.isRefreshing = true
            val list = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getPostApi()
                        .getPopularMovieListCoroutine(BuildConfig.THE_MOVIE_DB_API_TOKEN)
                    if (response.isSuccessful) {
                        val result = response.body()?.results
                        val result2 = result!!.subList(1, result.lastIndex)
                        if (!result2.isNullOrEmpty()) {
                            movieDao?.insertAll(result)
                        }
                        val response2 = RetrofitService.getPostApi().getFavouriteMoviesCoroutine(
                            accountId,
                            BuildConfig.THE_MOVIE_DB_API_TOKEN,
                            sessionId
                        )
                        if (response2.isSuccessful) {
                            val result3 = response2.body()?.results
                            for (m in result3!!) {
                                m.liked = 1
                            }
                            if (!result3.isNullOrEmpty()) {
                                movieDao?.insertAll(result3)
                            }
                        }
                        result2
                    } else {
                        movieDao?.getAll() ?: emptyList()
                    }
                } catch (e: Exception) {
                    movieDao?.getAll() ?: emptyList()
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