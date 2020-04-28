package com.example.movie.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movie.DetailActivity
import com.example.movie.R
import com.example.movie.model.Movie

class LikeMoviesAdapter (
    var context: Context,
    var moviesList: List<Movie>? = null
) : RecyclerView.Adapter<LikeMoviesAdapter.LikeMovieViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): LikeMovieViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.movie_card, p0, false)
        return LikeMovieViewHolder(view)
    }

    override fun getItemCount(): Int = moviesList?.size ?: 0

    override fun onBindViewHolder(viewHolder: LikeMovieViewHolder, i: Int) {
        viewHolder.bind(moviesList?.get(i))
    }


    inner class LikeMovieViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(post: Movie?) {
            val title = view.findViewById<TextView>(R.id.title)
            val description = view.findViewById<TextView>(R.id.description)
            val thumbnail = view.findViewById<ImageView>(R.id.thumbnail)

            title.text = post?.original_title
            val vote = post?.overview
            description.text = vote.toString()

            Glide.with(context)
                .load(post?.getPosterPath())
                .into(thumbnail)

            view.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("movie_id",post?.id)
                intent.putExtra("original_title", post?.original_title)
                intent.putExtra("poster_path", post?.poster_path)
                intent.putExtra("overview", post?.overview)
                intent.putExtra("vote_average", (post?.vote_average).toString())
                intent.putExtra("release_date", post?.release_date)
                view.context.startActivity(intent)
            }
        }
    }


}

