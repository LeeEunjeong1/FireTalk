package com.example.firetalk.ui.main.home

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firetalk.model.Friend
import com.example.firetalk.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendViewModel : ViewModel() {

    private var database : DatabaseReference = Firebase.database.reference

    var friend = MutableLiveData<MutableList<Friend>>()
    val friendData get() = friend

    var profile = MutableLiveData<MutableList<Friend>>()
    val myProfile get() = profile

    var error = MutableLiveData<String>()
    val errorData get() = error

    fun  getFriend(){
        CoroutineScope(Dispatchers.Default).launch {
            try{
                val myUid = UserPreferences.id
                val userList: MutableList<Friend> = mutableListOf()
                val friendList : MutableList<Friend> = mutableListOf()
                database.child("users").addValueEventListener(object :
                    ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        errorData.postValue(error.message)
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(data in snapshot.children){
                            val item = data.getValue<Friend>()!!
                            //내 프로필
                            if(item.uid.equals(myUid)){
                                userList.add(Friend(item.email,item.image,item.name,item.uid))
                                profile.postValue(userList)
                                Log.d("continue",item.name!!)
                                continue
                            }
                            friendList.add(Friend(item.email,item.image,item.name,item.uid))
                            friend.postValue(friendList)
                            Log.d("for",item.name!!)
                        }
                    }
                })
            }catch (e:Exception){
                error.postValue(e.message)
            }
        }
    }
}