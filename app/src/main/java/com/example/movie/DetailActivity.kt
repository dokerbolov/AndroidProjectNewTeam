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
import com.example.movie.model.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class DetailActivity : AppCompatActivity() {
    lateinit var nameofMovie: TextView
    lateinit var plotSynopsis: TextView
    lateinit var userRating: TextView
    lateinit var releaseDate: TextView
    lateinit var imageView: ImageView
    lateinit var toolbar: Toolbar
    var movie_id: Int? = null
    var account_id: Int? = null
    var session_id: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
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
            if (drawable.constantState!!.equals(getDrawable(R.drawable.ic_favorite_border)?.constantState))
            {
                item.icon = getDrawable(R.drawable.ic_favorite_liked)
                likeMovie(true)
            } else
            {
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
    }

    private fun initIntents() {
        val intent = getIntent()
        if (intent.hasExtra("original_title")) {
            session_id = Singleton.getSession()
            account_id = Singleton.getAccountId()
            movie_id = getIntent().extras?.getInt("movie_id")
            val thumbnail =
                "https://image.tmdb.org/t/p/w500" + getIntent().getExtras()?.getString("poster_path")
            val movieName = getIntent().getExtras()?.getString("original_title")
            val synopsis = getIntent().getExtras()?.getString("overview")
            val rating = getIntent().getExtras()?.getString("vote_average")
            val sateOfRelease = getIntent().getExtras()?.getString("release_date")

            Glide.with(this)
                .load(thumbnail)
                .into(imageView)

            nameofMovie.text = movieName
            plotSynopsis.text = synopsis
            userRating.text = rating
            releaseDate.text = sateOfRelease

        } else {
            Toast.makeText(this, "No API Data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasLike() {
        RetrofitService.getPostApi()
            .hasLike(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN, session_id)
            .enqueue(object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    Log.d("TAG", response.toString())
                    if (response.isSuccessful) {
                        val gson = Gson()
                        var like = gson.fromJson(
                            response.body(),
                            FavResponse::class.java
                        ).favorite
                        if (like)
                            toolbar.menu.findItem(R.id.favourite).icon =
                            getDrawable(R.drawable.ic_favorite_liked)
                        else
                            toolbar.menu.findItem(R.id.favourite).icon =
                            getDrawable(R.drawable.ic_favorite_border)
                    }

                }
            })

    }

    private fun likeMovie(favourite: Boolean) {
        val body = JsonObject().apply {
            addProperty("media_type", "movie")
            addProperty("media_id", movie_id)
            addProperty("favorite", favourite)
        }

        RetrofitService.getPostApi()
            .rate(account_id, BuildConfig.THE_MOVIE_DB_API_TOKEN, session_id, body)
            .enqueue(object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {

                    if (response.isSuccessful) {
                        if (favourite)
                            Toast.makeText(
                                this@DetailActivity,
                                "Movie has been added to favourites",
                                Toast.LENGTH_LONG
                            ).show()
                        else
                            Toast.makeText(
                                this@DetailActivity,
                                "Movie has been removed from favourites",
                                Toast.LENGTH_LONG
                            ).show()

                    }

                }
            })
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