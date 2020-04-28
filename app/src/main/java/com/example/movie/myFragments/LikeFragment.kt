package com.example.movie.myFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.movie.BuildConfig
import com.example.movie.R
import com.example.movie.adapter.LikeMoviesAdapter
import com.example.movie.api.RetrofitService
import com.example.movie.model.Movie
import com.example.movie.model.MovieResponse
import com.example.movie.model.Singleton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class LikeFragment : Fragment() {


    lateinit var relativeLayout: RelativeLayout
    lateinit var commentsIc: ImageView
    lateinit var timeIc: ImageView
    lateinit var recyclerView: RecyclerView
    private var dateTv: TextView? = null
    private var commentsTv: TextView? = null
    private var bigPictv: TextView? = null
    private var bigPicCardIm: ImageView? = null
    private var postAdapter: LikeMoviesAdapter? = null
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var movieList: List<Movie>
    lateinit var movie: Movie
    private var rootView: View? = null
    var session_id = Singleton.getSession()
    var account_id = Singleton.getAccountId()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

    fun initViews() {
        bigPicCardIm?.visibility = View.INVISIBLE
        movieList = ArrayList<Movie>()
        postAdapter = activity?.applicationContext?.let { LikeMoviesAdapter(it, movieList) }!!
        recyclerView.layoutManager = GridLayoutManager(activity, 1)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = postAdapter
        postAdapter?.notifyDataSetChanged()
        loadJSON()
    }


    fun loadJSON() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                return;
            }
             RetrofitService.getPostApi()
                .getFavoriteMovies(account_id, BuildConfig.THE_MOVIE_DB_API_TOKEN, session_id)
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

    private fun bindView()
    {
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

}
