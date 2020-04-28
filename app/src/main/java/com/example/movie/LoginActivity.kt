package com.example.movie


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movie.api.RequestToken
import com.example.movie.api.RetrofitService
import com.example.movie.api.Session
import com.example.movie.model.MyAccount
import com.example.movie.model.Singleton
import com.example.movie.model.User
import com.example.movie.view.MainActivity
import com.example.movie.view.RegistrationActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.reflect.Type
import kotlin.coroutines.CoroutineContext

class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var login: Button
    private lateinit var register: Button
    private lateinit var preferences: SharedPreferences
    private lateinit var requestToken: String
    private lateinit var newRequestToken: String
    private lateinit var emailValue: String
    private lateinit var passwordValue: String
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bindView()
        stayLogged()

        login.setOnClickListener {
            loginCoroutine()
        }

        register.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun loginCoroutine() {
        emailValue = email.text.toString()
        passwordValue = password.text.toString()
        if (emailValue != "" && passwordValue != "") {
            launch {
                val response = RetrofitService.getPostApi()
                    .getRequestTokenCorountine(BuildConfig.THE_MOVIE_DB_API_TOKEN)
                if (response.isSuccessful) {
                    requestToken = response.body()?.requestToken!!
                    val body = JsonObject().apply {
                        addProperty("username", emailValue)
                        addProperty("password", passwordValue)
                        addProperty("request_token", requestToken)
                    }

                    val responseLogin = RetrofitService.getPostApi()
                        .loginCoroutune(BuildConfig.THE_MOVIE_DB_API_TOKEN, body)

                    if (responseLogin.isSuccessful) {
                        var gson = Gson()
                        var newRequesttoken = gson.fromJson(
                            responseLogin.body(),
                            RequestToken::class.java
                        )
                        newRequestToken = newRequesttoken.requestToken
                        val responseSession = RetrofitService.getPostApi()
                            .getSessionCoroutine(BuildConfig.THE_MOVIE_DB_API_TOKEN, body)

                        if (responseSession.isSuccessful) {
                            var gson = Gson()
                            var newSession =
                                gson.fromJson(
                                    responseSession.body(),
                                    Session::class.java
                                )
                            val sessionId = newSession.sessionId
                            val response = RetrofitService.getPostApi()
                                .getAccountCoroutine(BuildConfig.THE_MOVIE_DB_API_TOKEN, sessionId)
                            if (response.isSuccessful) {
                                var gson = Gson()
                                var newIdAcc =
                                    gson.fromJson(response.body(), MyAccount::class.java)
                                var idAcc = newIdAcc.id
                                var user = User(emailValue, sessionId, idAcc)
                                var MySingleton = Singleton.create(emailValue, sessionId, idAcc)
                                val json1 = gson.toJson(user)
                                preferences = this@LoginActivity.getSharedPreferences("Username", 0)
                                preferences.edit().putString("user", json1).commit()
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this@LoginActivity, "Type email and password", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun bindView() {
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        register = findViewById(R.id.register)
    }

    private fun stayLogged() {
        try {
            preferences = this@LoginActivity.getSharedPreferences("Username", 0)
            var gsonGen = Gson()
            var json: String? = preferences.getString("user", null)
            var type: Type = object : TypeToken<User>() {}.type
            var user = gsonGen.fromJson<User>(json, type)

            if (user.sessionId != "") {
                var MySingleton =
                    Singleton.create(
                        user.username,
                        user.sessionId,
                        user.accountId
                    )
                val intent =
                    Intent(
                        this@LoginActivity,
                        MainActivity::class.java
                    )

                startActivity(
                    intent
                )

            }
        } catch (e: Exception) {
            Toast.makeText(this@LoginActivity, "Please, log in", Toast.LENGTH_LONG)
                .show()
        }

    }
}

