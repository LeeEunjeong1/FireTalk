package com.example.firetalk.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.firetalk.databinding.ActivitySplashBinding
import com.example.firetalk.ui.main.MainActivity
import com.example.firetalk.utils.UserPreferences

class SplashActivity :AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UserPreferences.init(this)

        Handler(Looper.getMainLooper()).postDelayed({
            isUser()
        },2000)


    }

    private fun isUser(){
        if(UserPreferences.id != ""){
            val intent = Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this,LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
    }


}