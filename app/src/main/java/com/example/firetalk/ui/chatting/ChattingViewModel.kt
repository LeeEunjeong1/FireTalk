package com.example.firetalk.ui.chatting

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firetalk.model.Chat
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
import java.text.SimpleDateFormat
import java.util.*

class ChattingViewModel: ViewModel() {
    private var fireDatabase : DatabaseReference = Firebase.database.reference
    private var myUid : String? = null
    private var chatRoomUid : String ? = null
    private var friend : Friend? = null

    var friendName1 = MutableLiveData<String>()
    val friendName get() = friendName1

    var friendInfo1 = MutableLiveData<MutableList<String>>()
    val friendInfo get() = friendInfo1

    var error1 = MutableLiveData<String>()
    val error get() = error1

    var message = MutableLiveData<MutableList<Chat.Comment>>()
    val chattingMessage get() = message

    var isSuccess1 = MutableLiveData<Boolean>()
    val isSuccess get() = isSuccess1


    fun getFriendName(friendUid: String){
        CoroutineScope(Dispatchers.Default).launch {
            try{
                fireDatabase.child("users").child(friendUid).addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        error1.postValue(error.message)
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        friendName1.postValue(snapshot.getValue<User>()?.name)
                        Log.d("friendName",snapshot.getValue<User>()?.name!!)
                    }
                })
            }catch (e:Exception){
                error1.postValue(e.message)
            }
        }
    }
    fun checkChatRoom(friendUid: String) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                myUid = UserPreferences.id
                fireDatabase.child("chatrooms").orderByChild("users/$myUid").equalTo(true)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (item in snapshot.children) {
                                val chatModel = item.getValue<Chat>()
                                if (chatModel?.users!!.containsKey(friendUid)) {
                                    chatRoomUid = item.key
                                    getFriendInfo(friendUid)
                                }
                            }
                        }
                    })
            } catch (e: Exception) {
                error1.postValue(e.message)
            }

        }
    }
    fun getFriendInfo(friendUid: String){
        CoroutineScope(Dispatchers.Default).launch {
            try {
                fireDatabase.child("users").child(friendUid).addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            error1.postValue(error.message)
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            friend = snapshot.getValue<Friend>()
                            val friendImage = friend?.image
                            val friendName = friend?.name
                            getMessageList(friendImage!!, friendName!!)
                        }
                    })
            } catch (e: Exception) {
                error1.postValue(e.message)
            }
        }

    }
    fun getMessageList(friendImage:String, friendName:String){
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val messageList: MutableList<Chat.Comment> = mutableListOf()
                val friendIn: MutableList<String> = mutableListOf()
                fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        error1.postValue(error.message)
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(data in snapshot.children){
                            val item = data.getValue<Chat.Comment>()!!
                            messageList.add(Chat.Comment(item.uid,item.message,item.time))
                            friendIn.add(friendImage)
                            friendIn.add(friendName)
                            message.postValue(messageList)
                            friendInfo1.postValue(friendIn)
                        }
                    }
                })
            } catch (e: Exception) {
                error1.postValue(e.message)
            }
        }
    }
    fun doChatting(friendUid: String,sendMsg : String){
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm", Locale.getDefault())
        val curTime = dateFormat.format(Date(time)).toString()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val chatModel = Chat()
                chatModel.users[myUid.toString()] = true
                chatModel.users[friendUid] = true
                val comment = Chat.Comment(myUid,sendMsg, curTime)
                if(chatRoomUid == null){
                    fireDatabase.child("chatrooms").push().setValue(chatModel).addOnSuccessListener {
                        //채팅방 생성
                        checkChatRoom(friendUid)
                        //메세지 보내기
                        Handler(Looper.getMainLooper()).postDelayed({
                            fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
                        },1000L)
                    }
                }else{
                    fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
                }
                isSuccess1.postValue(true)
            }catch (e:Exception){
                isSuccess1.postValue(false)
            }
        }
    }
}