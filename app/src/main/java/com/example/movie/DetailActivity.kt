package com.example.movie

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.movie.api.RetrofitService
import com.example.movie.database.MovieDao
import com.example.movie.database.MovieDatabase
import com.example.movie.model.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class DetailActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var nameofMovie: TextView
    private lateinit var plotSynopsis: TextView
    private lateinit var userRating: TextView
    private lateinit var releaseDate: TextView
    private lateinit var imageView: ImageView
    private lateinit var toolbar: Toolbar
    private lateinit var genre: TextView
    private var movie: Movie? = null
    private var movieId: Int? = null
    private var accountId: Int? = null
    private var sessionId: String? = ""
    private var movieDao: MovieDao? = null

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        movieDao = MovieDatabase.getDatabase(this).movieDao()
        bindView()
        initIntents()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        hasLike()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        if (item.itemId == R.id.favourite) {

            var drawable: Drawable = item.icon.current
            if (drawable.constantState?.equals(getDrawable(R.drawable.ic_favorite_border)?.constantState)!!) {
                item.icon = getDrawable(R.drawable.ic_favorite_liked)
                likeMovie(true)
            } else {
                item.icon = getDrawable(R.drawable.ic_favorite_border)
                likeMovie(false)
            }
        }
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }


    private fun bindView() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initCollapsingToolbar()
        imageView = findViewById(R.id.thumbnail_image_header)
        nameofMovie = findViewById(R.id.title)
        plotSynopsis = findViewById(R.id.plotsynopsis)
        userRating = findViewById(R.id.userrating)
        releaseDate = findViewById(R.id.releasedate)
        genre = findViewById(R.id.genre)
    }

    private fun initIntents() {
        val intent = getIntent()
        if (intent.hasExtra("original_title")) {
            sessionId = Singleton.getSession()
            accountId = Singleton.getAccountId()
            movieId = getIntent().extras?.getInt("movie_id")
            movie = getIntent().extras?.getSerializable("movie") as Movie

            val thumbnail = getIntent().getExtras()?.getString("poster_path")
            val movieName = getIntent().getExtras()?.getString("original_title")
            val synopsis = getIntent().getExtras()?.getString("overview")
            val rating = getIntent().getExtras()?.getString("vote_average")
            val sateOfRelease = getIntent().getExtras()?.getString("release_date")

            try {
                Glide.with(this)
                    .load(thumbnail)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading)
                    .into(imageView)
            } catch (e: Exception) {

                Glide.with(this)
                    .load(R.drawable.loading)
                    .into(imageView)
            }

            nameofMovie.text = movieName
            plotSynopsis.text = synopsis
            userRating.text = rating
            releaseDate.text = sateOfRelease

        } else {
            Toast.makeText(this, "No API Data", Toast.LENGTH_SHORT).show()
        }
    }


    private fun hasLike() {

        launch {
            val likeInt = withContext(Dispatchers.IO) {
                try {
                    val response = RetrofitService.getPostApi()
                        .hasLikeCoroutine(movieId, BuildConfig.THE_MOVIE_DB_API_TOKEN, sessionId)
                    Log.d("TAG", response.toString())
                    if (response.isSuccessful) {
                        val gson = Gson()
                        var like = gson.fromJson(
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

            if (likeInt == 1 || likeInt == 11)
                toolbar.menu.findItem(R.id.favourite).icon =
                    getDrawable(R.drawable.ic_favorite_liked)
            else
                toolbar.menu.findItem(R.id.favourite).icon =
                    getDrawable(R.drawable.ic_favorite_border)
        }
    }

    private fun likeMovie(favourite: Boolean) {
        launch {

            val body = JsonObject().apply {
                addProperty("media_type", "movie")
                addProperty("media_id", movieId)
                addProperty("favorite", favourite)
            }
            try {
                RetrofitService.getPostApi()
                    .rateCoroutine(accountId, BuildConfig.THE_MOVIE_DB_API_TOKEN, sessionId, body)
            } catch (e: Exception) {
            }
            if (favourite) {
                movie?.liked = 11
                movieDao?.insert(movie)
                Toast.makeText(
                    this@DetailActivity,
                    "Movie has been added to favourites",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                movie?.liked = 10
                movieDao?.insert(movie)
                Toast.makeText(
                    this@DetailActivity,
                    "Movie has been removed from favourites",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initCollapsingToolbar() {
        val collapse: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapse.title = " "
        val appBarLayout: AppBarLayout = findViewById(R.id.appbar)
        appBarLayout.setExpanded(true)

        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapse.title = getString(R.string.movie_details)
                    isShow = true
                } else if (isShow) {
                    collapse.title = " "
                    isShow = false
                }

            }
        })
    }
}