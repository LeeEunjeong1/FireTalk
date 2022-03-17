package com.example.firetalk.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firetalk.databinding.ActivityChattingBinding
import com.example.firetalk.utils.UserPreferences
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChattingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChattingBinding

    private lateinit var database : DatabaseReference
    private var friendUid : String? = null
    private var myUid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        friendUid = intent.getStringExtra("uid")
        myUid = UserPreferences.id
    }
}