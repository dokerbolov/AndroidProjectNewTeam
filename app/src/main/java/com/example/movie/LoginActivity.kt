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
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.lang.reflect.Type
import kotlin.coroutines.CoroutineContext

class LoginActivity : AppCompatActivity() {
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var login: Button
    lateinit var register: Button
    lateinit var preferences: SharedPreferences
    lateinit var requestToken: String
    lateinit var newRequestToken: String
    lateinit var emailValue: String
    lateinit var passwordValue: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bindView()
        stayLogged()

        login.setOnClickListener {
            login()
        }


        register.setOnClickListener {

            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)

        }
    }

    fun login() {
        emailValue = email.getText().toString()
        passwordValue = password.getText().toString()


        if (emailValue != "" && passwordValue != "") {
            RetrofitService.getPostApi().getRequestToken(BuildConfig.THE_MOVIE_DB_API_TOKEN)
                .enqueue(object : Callback<RequestToken> {
                    override fun onFailure(call: Call<RequestToken>, t: Throwable) {

                    }

                    override fun onResponse(
                        call: Call<RequestToken>,
                        response: Response<RequestToken>
                    ) {

                        if (response.isSuccessful) {

                            requestToken = response.body()?.requestToken!!

                            val body = JsonObject().apply {
                                addProperty("username", emailValue)
                                addProperty("password", passwordValue)
                                addProperty("request_token", requestToken)
                            }

                            RetrofitService.getPostApi()
                                .login(BuildConfig.THE_MOVIE_DB_API_TOKEN, body)
                                .enqueue(object : Callback<JsonObject> {
                                    override fun onFailure(
                                        call: Call<JsonObject>,
                                        t: Throwable
                                    ) {

                                    }

                                    override fun onResponse(
                                        call: Call<JsonObject>,
                                        response: Response<JsonObject>
                                    ) {

                                        if (response.isSuccessful) {

                                            var gson = Gson()
                                            var new_RequestToken = gson.fromJson(
                                                response.body(),
                                                RequestToken::class.java
                                            )
                                            newRequestToken = new_RequestToken.requestToken

                                            RetrofitService.getPostApi()
                                                .getSession(
                                                    BuildConfig.THE_MOVIE_DB_API_TOKEN,
                                                    body
                                                )
                                                .enqueue(object : Callback<JsonObject> {
                                                    override fun onFailure(
                                                        call: Call<JsonObject>,
                                                        t: Throwable
                                                    ) {

                                                    }

                                                    override fun onResponse(
                                                        call: Call<JsonObject>,
                                                        response: Response<JsonObject>
                                                    ) {

                                                        if (response.isSuccessful) {

                                                            var gson = Gson()
                                                            var new_session =
                                                                gson.fromJson(
                                                                    response.body(),
                                                                    Session::class.java
                                                                )

                                                            val sessionId =
                                                                new_session.sessionId

                                                            RetrofitService.getPostApi()
                                                                .getAccount(
                                                                    BuildConfig.THE_MOVIE_DB_API_TOKEN,
                                                                    sessionId
                                                                )
                                                                .enqueue(object :
                                                                    Callback<JsonObject> {
                                                                    override fun onFailure(
                                                                        call: Call<JsonObject>,
                                                                        t: Throwable
                                                                    ) {
                                                                        Toast.makeText(
                                                                            this@LoginActivity,
                                                                            "fail",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }

                                                                    override fun onResponse(
                                                                        call: Call<JsonObject>,
                                                                        response: Response<JsonObject>
                                                                    ) {

                                                                        if (response.isSuccessful) {


                                                                            var gson =
                                                                                Gson()
                                                                            var new_idAcc =
                                                                                gson.fromJson(
                                                                                    response.body(),
                                                                                    MyAccount::class.java
                                                                                )
                                                                            var idAcc =
                                                                                new_idAcc.id


                                                                            val user =
                                                                                User(
                                                                                    emailValue,
                                                                                    sessionId,
                                                                                    idAcc
                                                                                )
                                                                            var singleton =
                                                                                Singleton.create(
                                                                                    emailValue,
                                                                                    sessionId,
                                                                                    idAcc
                                                                                )
                                                                            val json1: String =
                                                                                gson.toJson(
                                                                                    user
                                                                                )
                                                                            preferences =
                                                                                this@LoginActivity.getSharedPreferences(
                                                                                    "Username",
                                                                                    0
                                                                                )
                                                                            preferences.edit()
                                                                                .putString(
                                                                                    "user",
                                                                                    json1
                                                                                ).commit()
                                                                            val intent =
                                                                                Intent(
                                                                                    this@LoginActivity,
                                                                                    MainActivity::class.java
                                                                                )

                                                                            startActivity(
                                                                                intent
                                                                            )


                                                                        }

                                                                    }
                                                                })


                                                        }

                                                    }
                                                })


                                        }

                                    }
                                })

                        }

                    }
                })


        } else {
            Toast.makeText(this@LoginActivity, "Empty email or password", Toast.LENGTH_LONG)
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
                var singleton =
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

        }

    }
}

