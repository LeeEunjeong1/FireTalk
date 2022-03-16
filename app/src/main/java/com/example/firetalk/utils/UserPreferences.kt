package com.example.firetalk.utils

import android.content.Context
import android.content.SharedPreferences

object UserPreferences {
    private const val PREF_NAME = "userData"
    private const val PREF_MODE = Context.MODE_PRIVATE
    private lateinit var sharedPreferences: SharedPreferences

    //key
    private const val ID = "id"

    var id: String
        get() = sharedPreferences.getString(
            ID, ""
        )!!
        set(value) = sharedPreferences.edit().putString(
            ID, value
        ).apply()

    fun init(context: Context){
        sharedPreferences = context.getSharedPreferences(
            PREF_NAME,
            PREF_MODE
        )
    }

    fun logout()
    {
        id=""
    }
}