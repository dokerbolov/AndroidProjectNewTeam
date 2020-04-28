package com.example.movie.myFragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.movie.view.DetailActivity
import com.example.movie.R
import com.example.movie.adapter.MoviesAdapter
import com.example.movie.model.Movie
import com.example.movie.model.Singleton
import com.example.movie.view_model.MovieListViewModel
import com.example.movie.view_model.ViewModelProviderFactory

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {
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
    private var movie: Movie? = null
    private var rootView: View? = null
    private var accountId: Int? = null
    private var sessionId: String? = ""
    private lateinit var movieListViewModel: MovieListViewModel
    private var newMovieList: List<Movie>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.activity_main, container, false) as ViewGroup
        bindViews()
        viewModelProvider()
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
        initViews()

        movieListViewModel.liveData.observe(this, Observer { result ->
            when (result) {
                is MovieListViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MovieListViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MovieListViewModel.State.Result -> {
                    postAdapter?.moviesList = result.list?.subList(1, result.list.size - 1)
                    postAdapter?.notifyDataSetChanged()
                    newMovieList = result.list
                    bigPicCard()
                }
            }
        })
        return rootView
    }

    private fun intentFun() {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("movie_id", movie?.id)
        intent.putExtra("original_title", movie?.original_title)
        intent.putExtra("poster_path", movie?.getPosterPath())
        intent.putExtra("overview", movie?.overview)
        intent.putExtra("vote_average", (movie?.vote_average).toString())
        intent.putExtra("release_date", movie?.release_date)
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
        postAdapter = activity?.applicationContext?.let { MoviesAdapter(it, movieList) }
        recyclerView.layoutManager = GridLayoutManager(activity, 1)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = postAdapter
        postAdapter?.notifyDataSetChanged()
        loadJSON()


    }

    private fun bigPicCard() {
        movie = newMovieList?.get(0)
        dateTv?.text = "март 30, 2020"
        commentsTv?.text = "0"
        bigPictv?.text = movie?.original_title
        bigPicCardIm?.visibility = View.VISIBLE
        context?.let {
            Glide.with(it)
                .load(movie?.getPosterPath())
                .into((rootView as ViewGroup).findViewById(R.id.main_big_pic))
        }
        commentsIc.visibility = View.VISIBLE
        timeIc.visibility = View.VISIBLE
        commentsIc.visibility = View.VISIBLE
        timeIc.visibility = View.VISIBLE
    }

    private fun loadJSON() {
        movieListViewModel.getMoviesList()
        bigPicCard()
    }

    private fun viewModelProvider() {
        sessionId = Singleton.getSession()
        accountId = Singleton.getAccountId()
        val viewModelProviderFactory = ViewModelProviderFactory(context = this.activity as Context)
        movieListViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(MovieListViewModel::class.java)
    }
}