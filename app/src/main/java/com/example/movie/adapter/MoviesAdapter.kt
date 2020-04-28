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
import kotlinx.android.synthetic.main.movie_card.view.*

class MoviesAdapter(
    var context: Context,
    var moviesList: List<Movie>? = null
) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_card, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = moviesList?.size ?: 0

    override fun onBindViewHolder(viewHolder: MovieViewHolder, i: Int) {
        viewHolder.bind(moviesList?.get(i))
    }


    inner class MovieViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(movie: Movie?) {
            val title = view.findViewById<TextView>(R.id.title)
            val description = view.findViewById<TextView>(R.id.description)
            val thumbnail = view.findViewById<ImageView>(R.id.thumbnail)


            title.text = movie?.original_title
            val vote = movie?.overview
            description.text = vote.toString()

            Glide.with(context)
                .load(movie?.getPosterPath())
                .into(thumbnail)

            view.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("movie_id", movie?.id)
                intent.putExtra("original_title", movie?.original_title)
                intent.putExtra("poster_path", movie?.poster_path)
                intent.putExtra("overview", movie?.overview)
                intent.putExtra("vote_average", (movie?.vote_average).toString())
                intent.putExtra("release_date", movie?.release_date)
                view.context.startActivity(intent)
            }
        }
    }


}

