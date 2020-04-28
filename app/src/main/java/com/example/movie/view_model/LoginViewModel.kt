package com.example.movie.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie.BuildConfig
import com.example.movie.api.RequestToken
import com.example.movie.api.RetrofitService
import com.example.movie.api.Session
import com.example.movie.model.MyAccount
import com.example.movie.model.User
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class LoginViewModel(private val context: Context) : ViewModel(), CoroutineScope {
    private val job = Job()
    var state = MutableLiveData<State>()
    private lateinit var requestToken: String
    private lateinit var newRequestToken: String
    private var json: String = ""

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun makeToken(emailValue: String, passwordValue: String) {
        state.value = State.ShowLoading
        launch {
            val response = RetrofitService.getPostApi()
                .getRequestTokenCorountine(BuildConfig.THE_MOVIE_DB_API_TOKEN)
            if (response.isSuccessful) {
                requestToken = response.body()?.requestToken.toString()
                responseToken(emailValue, passwordValue)
            } else {
                state.value = State.BadResult
                state.value = State.HideLoading
            }
        }
    }

    private fun responseToken(emailValue: String, passwordValue: String) {
        launch {
            val body = JsonObject().apply {
                addProperty("username", emailValue)
                addProperty("password", passwordValue)
                addProperty("request_token", requestToken)
            }
            val responseLogin = RetrofitService.getPostApi()
                .loginCoroutune(BuildConfig.THE_MOVIE_DB_API_TOKEN, body)

            if (responseLogin.isSuccessful) {
                val newRequesttoken = Gson().fromJson(
                    responseLogin.body(),
                    RequestToken::class.java
                )
                newRequestToken = newRequesttoken.requestToken
                getSession(emailValue, body)

            } else {
                state.value = State.BadResult
                state.value = State.HideLoading
            }
        }
    }

    private fun getSession(emailValue: String, body: JsonObject) {
        launch {
            val responseSession = RetrofitService.getPostApi()
                .getSessionCoroutine(BuildConfig.THE_MOVIE_DB_API_TOKEN, body)

            if (responseSession.isSuccessful) {
                val newSession =
                    Gson().fromJson(
                        responseSession.body(),
                        Session::class.java
                    )
                val sessionId = newSession.sessionId
                getAccountId(emailValue, sessionId)
            } else {
                state.value = State.BadResult
                state.value = State.HideLoading
            }

        }
    }

    private fun getAccountId(emailValue: String, sessionId: String) {
        launch {
            val response = RetrofitService.getPostApi()
                .getAccountCoroutine(BuildConfig.THE_MOVIE_DB_API_TOKEN, sessionId)
            if (response.isSuccessful) {
                val newIdAcc =
                    Gson().fromJson(response.body(), MyAccount::class.java)
                val idAcc = newIdAcc.id
                val user = User(emailValue, sessionId, idAcc)
                json = Gson().toJson(user)
                state.value = State.Result(json)
            } else {
                state.value = State.BadResult
                state.value = State.HideLoading
            }
        }
        state.value = State.HideLoading
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val json: String?) : State()
        object BadResult : State()
    }
}