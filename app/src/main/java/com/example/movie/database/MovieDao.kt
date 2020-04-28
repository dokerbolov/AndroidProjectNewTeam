package com.example.movie.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movie.model.Movie

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: Movie?)

    @Query("SELECT*FROM movies_table")
    fun getAll(): List<Movie>

    @Query("SELECT*FROM movies_table where liked=:liked")
    fun getMovieOffline(liked: Int?): List<Movie>

    @Query("SELECT*FROM movies_table where liked=1 or liked=11")
    fun getAllLiked(): List<Movie>

    @Query("SELECT liked FROM movies_table where id=:id")
    fun getLiked(id: Int?): Int

    @Query("SELECT id FROM movies_table where liked=:liked")
    fun getIdOffline(liked: Int?): List<Int>
}