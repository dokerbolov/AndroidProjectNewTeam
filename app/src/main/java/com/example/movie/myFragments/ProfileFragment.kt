package com.example.movie.myFragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.movie.BuildConfig
import com.example.movie.LoginActivity
import com.example.movie.R
import com.example.movie.api.RetrofitService
import com.example.movie.model.Singleton
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ProfileFragment : Fragment(), CoroutineScope by MainScope() {
    private lateinit var preferences: SharedPreferences
    private lateinit var nameInfo: TextView
    private lateinit var emailInfo: TextView
    private lateinit var logout: Button
    private lateinit var editor: SharedPreferences.Editor
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.activity_profile, container, false) as ViewGroup
        preferences = context?.getSharedPreferences("Username", 0)!!
        bindView(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        logout.setOnClickListener {
            editor.clear().commit()
            launch {
                val body: JsonObject = JsonObject().apply {
                    addProperty("session_id", Singleton.getSession())
                }
                val response = RetrofitService.getPostApi()
                    .deleteSessionCoroutine(BuildConfig.THE_MOVIE_DB_API_TOKEN, body)
                if (response.isSuccessful) {
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


    private fun bindView(rootView: ViewGroup) {
        nameInfo = rootView.findViewById(R.id.name)
        emailInfo = rootView.findViewById(R.id.email)
        logout = rootView.findViewById(R.id.logout)
        editor = preferences.edit()
    }

    private fun initViews() {
        val authorizedName = Singleton.getUserName()
        val authorizedEmail = Singleton.getUserName() + "@mail.ru"
        nameInfo.text = authorizedName
        emailInfo.text = authorizedEmail
    }


}