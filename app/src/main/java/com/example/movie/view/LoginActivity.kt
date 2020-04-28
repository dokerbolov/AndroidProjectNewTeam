package com.example.movie.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.movie.R
import com.example.movie.model.Singleton
import com.example.movie.model.User
import com.example.movie.view_model.LoginViewModel
import com.example.movie.view_model.ViewModelProviderFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import kotlin.Exception

class LoginActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var login: Button
    private lateinit var register: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var preferences: SharedPreferences
    private var data: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val viewModelProviderFactory = ViewModelProviderFactory(context = this)
        loginViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(LoginViewModel::class.java)
        bindView()
        stayLogged()
        loginViewModel.state.observe(this, Observer {
            when (it) {
                is LoginViewModel.State.ShowLoading -> {
                    progressBar.visibility = ProgressBar.VISIBLE
                }
                is LoginViewModel.State.HideLoading -> {
                    progressBar.visibility = ProgressBar.INVISIBLE
                }
                is LoginViewModel.State.BadResult -> {
                    check()
                }
                is LoginViewModel.State.Result -> {
                    loginCoroutine(it.json)

                }
            }
        })
        register.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
        login.setOnClickListener {
            loginViewModel.makeToken(email.text.toString(), password.text.toString())
        }

    }

    private fun check() {
        if (email.text.toString() == "" || password.text.toString() == "") {
            Toast.makeText(this@LoginActivity, this@LoginActivity.getString(R.string.empty), Toast.LENGTH_LONG)
                .show()
        } else {
            Toast.makeText(this@LoginActivity, this@LoginActivity.getString(R.string.wrong), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun bindView() {
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        register = findViewById(R.id.register)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun loginCoroutine(data: String?) {
        try {
            preferences = this@LoginActivity.getSharedPreferences("Username", 0)
            preferences.edit().putString("user", data).commit()
            val gsonGen = Gson()
            val type: Type = object : TypeToken<User>() {}.type
            val user = gsonGen.fromJson<User>(data, type)
            openApp(user)
        } catch (e: Exception) {
        }
    }

    private fun stayLogged() {
        try {
            preferences = this@LoginActivity.getSharedPreferences("Username", 0)
            val gsonGen = Gson()
            val json: String? = preferences.getString("user", null)
            val type: Type = object : TypeToken<User>() {}.type
            val user = gsonGen.fromJson<User>(json, type)

            openApp(user)
        } catch (e: Exception) {
        }
    }

    private fun openApp(user: User) {
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
    }
}

