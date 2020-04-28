package com.example.movie

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class RegistrationActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    lateinit var name: EditText
    private lateinit var login: Button
    private lateinit var register: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        register = findViewById(R.id.register)
        name = findViewById(R.id.name)

        login.setOnClickListener {
            onBackPressed()
        }
    }
}