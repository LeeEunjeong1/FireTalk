package com.example.firetalk.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.firetalk.databinding.ActivitySplashBinding
import com.example.firetalk.ui.login.LoginActivity
import com.example.firetalk.ui.main.MainActivity
import com.example.firetalk.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity :AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        UserPreferences.init(this)

        Handler(Looper.getMainLooper()).postDelayed({
            isUser()
        },2000)


    }

    private fun isUser(){
        if(UserPreferences.id!=""){
            Log.d("is_user",UserPreferences.id)
            val intent = Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }else{
            Log.d("is_user2",UserPreferences.id)
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
    }


}