package com.example.firetalk.ui.main.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firetalk.model.Chat
import com.example.firetalk.utils.UserPreferences
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

class ChatViewModel: ViewModel() {
    private var database : DatabaseReference = Firebase.database.reference

    var chat = MutableLiveData<MutableList<Chat>>()
    val chatList get() = chat

    var error1 = MutableLiveData<String>()
    val errorData get() = error1

    fun getChat(){

        val myUid = UserPreferences.id
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val chatting: MutableList<Chat> = mutableListOf()
                database.child("chatrooms").orderByChild("users/$myUid").equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        error1.postValue(error.message)
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(data in snapshot.children){
                            val item = data.getValue<Chat>()!!
                            chatting.add(Chat(item.users,item.comments))
                            chat.postValue(chatting)
                        }
                    }
                })
            }catch (e:Exception){
                error1.postValue(e.message)
            }
        }

    }
}