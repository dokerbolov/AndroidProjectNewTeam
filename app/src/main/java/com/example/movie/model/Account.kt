package com.example.movie.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MyAccount (
    @SerializedName("avatar")
    val avatar: Avatar,
    @SerializedName("id")
    val id: Int,
    @SerializedName("iso_639_1")
    val iso_639_1: String,
    @SerializedName("iso_3166_1")
    val iso_3166_1: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("include_adult")
    val include_adult: Boolean,
    @SerializedName("username")
    val username: String
):Serializable

data class Avatar(
    @SerializedName("gravatar")
    val gravatar: Gravatar
)
data class Gravatar(
    @SerializedName("hash")
    val hash: String
)