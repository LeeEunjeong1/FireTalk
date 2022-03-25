package com.example.firetalk.ui.main.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.firetalk.model.Friend
import com.example.firetalk.model.User
import com.example.firetalk.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel(){
    private lateinit var auth: FirebaseAuth
    private lateinit var fireDatabase : DatabaseReference

    var _profileData = MutableLiveData<MutableList<User>>()
    val profileData get() = _profileData

    var _error = MutableLiveData<String>()
    val errorData get() = _error
    init{
        getUserProfile()
    }
    private  fun getUserProfile(){
        auth = Firebase.auth
        fireDatabase = Firebase.database.reference
        CoroutineScope(Dispatchers.Default).launch {
            try{
                val userList: MutableList<User> = mutableListOf()
                fireDatabase.child("users").child(UserPreferences.id).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        errorData.postValue(error.message)
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userProfile = snapshot.getValue<User>()!!
                        userList.add(User(userProfile.email,userProfile.name,userProfile.image,userProfile.uid))
                        _profileData.postValue(userList)
                    }
                })
                }catch (e:Exception){
                    errorData.postValue(e.message)
            }
        }
    }
}
