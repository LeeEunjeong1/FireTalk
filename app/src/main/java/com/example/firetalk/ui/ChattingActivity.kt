package com.example.firetalk.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firetalk.databinding.ActivityChattingBinding


class ChattingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChattingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}