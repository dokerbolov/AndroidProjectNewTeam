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
import okhttp3.internal.notify
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class ProfileFragment : Fragment() {

    lateinit var preferences: SharedPreferences
    lateinit var nameInfo: TextView
    lateinit var emailInfo: TextView
    lateinit var logout: Button
    lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

            editor.clear().commit()

            val body: JsonObject = JsonObject().apply {
                addProperty("session_id", Singleton.getSession())
            }
            RetrofitService.getPostApi().deleteSession(BuildConfig.THE_MOVIE_DB_API_TOKEN, body)
                .enqueue(object : Callback<JsonObject> {
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    }
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        if (response.isSuccessful) {
                            val intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                })
        }
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