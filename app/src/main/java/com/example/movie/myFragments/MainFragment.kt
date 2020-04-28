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
import com.example.movie.model.Movie
import com.example.movie.model.MovieResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainFragment() : Fragment() {

    private var relativeLayout: RelativeLayout? = null
    private var a: Int = 0
    lateinit var commentsIc: ImageView
    lateinit var timeIc: ImageView
    lateinit var recyclerView: RecyclerView
    private var dateTv: TextView? = null
    private var commentsTv: TextView? = null
    private var bigPictv: TextView? = null
    private var bigPicCardIm: ImageView? = null
    private var postAdapter: MoviesAdapter? = null
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var movieList: List<Movie>
    lateinit var movie: Movie
    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

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
        intent.putExtra("poster_path", movie.poster_path)
        intent.putExtra("overview", movie.overview)
        intent.putExtra("vote_average", (movie.vote_average).toString())
        intent.putExtra("release_date", movie.release_date)
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
        movieList = ArrayList<Movie>()
        postAdapter = activity?.applicationContext?.let { MoviesAdapter(it, movieList) }!!
        recyclerView.layoutManager = GridLayoutManager(activity, 1)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = postAdapter
        postAdapter?.notifyDataSetChanged()
        bigPicCard()
        loadJSON()
    }

    private fun bigPicCard() {

        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                return;
            }
            RetrofitService.getPostApi().getPopularMovieList(BuildConfig.THE_MOVIE_DB_API_TOKEN)
                .enqueue(object : Callback<MovieResponse> {
                    override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                        swipeRefreshLayout.isRefreshing = false
                        a = 1
                    }
                    override fun onResponse(
                        call: Call<MovieResponse>,
                        response: Response<MovieResponse>
                    ) {
                        if (response.isSuccessful) {
                            val list = response.body()?.results
                        movie = list!!.first()
                        dateTv?.text = "март 30, 2020"
                        commentsTv?.text = "0"
                        bigPictv?.text = movie.original_title
                        bigPicCardIm?.visibility = View.VISIBLE
                        Glide.with(rootView!!.context)
                            .load(movie.getPosterPath())
                            .into((rootView as ViewGroup).findViewById(R.id.main_big_pic))
                    }
                        swipeRefreshLayout.isRefreshing = false

                    }
                })
        } catch (e: Exception) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT)
        }

        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                return;
            }
            RetrofitService.getPostApi().getPopularMovieList(BuildConfig.THE_MOVIE_DB_API_TOKEN)
                .enqueue(object : Callback<MovieResponse> {
                    override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                        swipeRefreshLayout.isRefreshing = false
                        a = 1
                    }

                    override fun onResponse(
                        call: Call<MovieResponse>,
                        response: Response<MovieResponse>
                    ) {
                        if (response.isSuccessful) {
                            val list = response.body()?.results
                            movie = list!!.first()
                            dateTv?.text = "март 30, 2020"
                            commentsTv?.text = "0"
                            bigPictv?.text = movie.original_title
                            bigPicCardIm?.visibility = View.VISIBLE
                            Glide.with(rootView!!.context)
                                .load(movie.getPosterPath())
                                .into((rootView as ViewGroup).findViewById(R.id.main_big_pic))
                        }
                        swipeRefreshLayout.isRefreshing = false

                    }
                })


        } catch (e: Exception) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT)
        }

        commentsIc.visibility = View.VISIBLE
        timeIc.visibility = View.VISIBLE
    }


    private fun loadJSON() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                return;
            }
            RetrofitService.getPostApi().getPopularMovieList(BuildConfig.THE_MOVIE_DB_API_TOKEN)
                .enqueue(object : Callback<MovieResponse> {
                    override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                        swipeRefreshLayout.isRefreshing = false
                    }
                    override fun onResponse(
                        call: Call<MovieResponse>,
                        response: Response<MovieResponse>
                    ) {
                        if (response.isSuccessful) {
                            val list = response.body()?.results
                            val list2 = list!!.subList(1, list.lastIndex)
                            postAdapter?.moviesList = list2
                            postAdapter?.notifyDataSetChanged()
                        }
                        swipeRefreshLayout.isRefreshing = false
                    }
                })

        } catch (e: Exception) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show()
        }
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                return;
            }
            RetrofitService.getPostApi().getPopularMovieList(BuildConfig.THE_MOVIE_DB_API_TOKEN)
                .enqueue(object : Callback<MovieResponse> {
                    override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                        swipeRefreshLayout.isRefreshing = false
                    }
                    override fun onResponse(
                        call: Call<MovieResponse>,
                        response: Response<MovieResponse>
                    ) {
                        if (response.isSuccessful) {
                            val list = response.body()?.results
                            postAdapter?.moviesList = list
                            postAdapter?.notifyDataSetChanged()
                        }
                        swipeRefreshLayout.isRefreshing = false
                    }
                })

        } catch (e: Exception) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show()
        }

    }
}
