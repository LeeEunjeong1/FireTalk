package com.example.firetalk.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firetalk.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var bnv_main = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bnv_main.run { setOnItemSelectedListener {
            when(it.itemId) {
                R.id.tab1 -> {
                    // 다른 프래그먼트 화면으로 이동하는 기능
                    val restFragment = Fragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_layout, restFragment).commit()
                }
                R.id.tab2 -> {
                    val searchFragment = Fragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_layout, searchFragment).commit()
                }
                R.id.tab3 -> {
                    val reservationFragment = Fragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_layout, reservationFragment).commit()
                }
            }
            true
        }
            selectedItemId = R.id.tab1
        }
    }
}