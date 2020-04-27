package com.example.movie.model

import com.google.gson.annotations.SerializedName

data class FavResponse
    (
    @SerializedName("id")
    val id: Int,
    @SerializedName("favorite")
    val favorite: Boolean,
    @SerializedName("rated")
    val rated: Object,
    @SerializedName("watchlist")
    val watchlist: Boolean
)

data class RateClass(
    @SerializedName("value")
    val value: Int
)