package com.example.firetalk.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firetalk.R
import com.example.firetalk.ui.main.chat.ChatFragment
import com.example.firetalk.ui.main.home.HomeFragment
import com.example.firetalk.ui.main.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bnv_main = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bnv_main.run { setOnItemSelectedListener {
            when(it.itemId) {
                R.id.tab1 -> {
                    // 다른 프래그먼트 화면으로 이동하는 기능
                    val homeFragment = HomeFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_layout, homeFragment).commit()
                }
                R.id.tab2 -> {
                    val chatFragment = ChatFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_layout, chatFragment).commit()
                }
                R.id.tab3 -> {
                    val profileFragment = ProfileFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_layout, profileFragment).commit()
                }
            }
            true
        }
            selectedItemId = R.id.tab1
        }
    }
}