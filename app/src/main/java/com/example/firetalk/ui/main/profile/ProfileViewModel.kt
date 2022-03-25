package com.example.firetalk.ui.main.profile

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.DiskCacheAdapter
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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel(){
    private lateinit var auth: FirebaseAuth
    private lateinit var fireDatabase : DatabaseReference

    var _profileData = MutableLiveData<MutableList<User>>()
    val profileData get() = _profileData

    var _changeSuccess = MutableLiveData<Boolean>()
    val changeSuccess get() = _changeSuccess

    var _error = MutableLiveData<String>()
    val errorData get() = _error
    init{
        auth = Firebase.auth
        fireDatabase = Firebase.database.reference
    }
    fun getUserProfile(){
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
    fun changeProfileName(name: String){
        CoroutineScope(Dispatchers.Default).launch {
            try{
                fireDatabase.child("users/${UserPreferences.id}/name").setValue(name)
            }catch (e:Exception){
                _changeSuccess.postValue(false)
            }
        }
    }

    fun changeProfileImage(imageUri: Uri){
        CoroutineScope(Dispatchers.Default).launch {
            try{
                FirebaseStorage.getInstance().reference
                    .child("userImage/${UserPreferences.id}/photo").delete().addOnSuccessListener {
                        FirebaseStorage.getInstance().reference.child("userImage/${UserPreferences.id}/photo").putFile(imageUri).addOnSuccessListener {
                            FirebaseStorage.getInstance().reference.child("userImage/${UserPreferences.id}/photo").downloadUrl.addOnSuccessListener {
                                val photoUri : Uri = it
                                Log.d("profileImage",it.toString())
                                fireDatabase.child("users").child(UserPreferences.id).child("image").setValue(photoUri.toString())
                                _changeSuccess.postValue(true)
                            }
                        }
                    }
            }catch(e:Exception){
                _changeSuccess.postValue(false)
            }
        }
    }
    fun logout(){
        auth.signOut()
    }
}
