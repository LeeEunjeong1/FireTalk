package com.example.firetalk.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firetalk.databinding.ActivityChattingBinding
import com.example.firetalk.model.Chat
import com.example.firetalk.model.Friend
import com.example.firetalk.utils.UserPreferences
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ChattingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChattingBinding
    private var adapter = ChattingAdapter()

    private lateinit var fireDatabase : DatabaseReference
    private var friendUid : String? = null
    private var myUid : String? = null
    private var chatRoomUid : String ? = null

    private var friend : Friend? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChattingBinding.inflate(layoutInflater)

        friendUid = intent.getStringExtra("uid")
        myUid = UserPreferences.id

        fireDatabase = Firebase.database.reference
        initListener()
        setContentView(binding.root)
    }

    private fun initListener(){
        //메세지를 보낸 시간
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm", Locale.getDefault())
        val curTime = dateFormat.format(Date(time)).toString()

        with(binding){
            imageView.setOnClickListener{
                val chatModel = Chat()
                chatModel.users[myUid.toString()] = true
                chatModel.users[friendUid!!] = true

                val comment = Chat.Comment(myUid, sendMsg.text.toString(), curTime)
                if(chatRoomUid == null){
                    imageView.isEnabled = false
                    fireDatabase.child("chatrooms").push().setValue(chatModel).addOnSuccessListener {
                        //채팅방 생성
                        checkChatRoom()
                        //메세지 보내기
                        Handler(Looper.getMainLooper()).postDelayed({
                            fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
                            sendMsg.text = null
                        },1000L)
                    }
                }else{
                    fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
                    sendMsg.text = null
                }
            }
            checkChatRoom()
        }
    }

    private fun checkChatRoom(){
        fireDatabase.child("chatrooms").orderByChild("users/$myUid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext,"다시 한 번 시도해주세요.", Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for(item in snapshot.children){
                        val chatModel = item.getValue<Chat>()
                        if(chatModel?.users!!.containsKey(friendUid)){
                            chatRoomUid = item.key
                            binding.imageView.isEnabled = true
                            getFriendInfo()
                        }
                    }
                }
            })
    }
    private fun getFriendInfo(){
        binding.recyclerView.adapter = adapter
        fireDatabase.child("users").child(friendUid.toString()).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext,"다시 한 번 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    friend = snapshot.getValue<Friend>()
                    val friendImage = friend?.image
                    val friendName = friend?.name
                    binding.name.text = friend?.name
                    getMessageList(friendImage!!,friendName!!)
                }
            })
    }
    private fun getMessageList(friendImage: String,friendName:String){
        fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"다시 한 번 시도해주세요.", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                adapter.clearList()
                for(data in snapshot.children){
                    val item = data.getValue<Chat.Comment>()
                    adapter.setChattingList(item!!)
                }
                adapter.setFriend(friendImage,friendName)
                binding.recyclerView.scrollToPosition(adapter.itemCount-1)
            }
        })
    }
}