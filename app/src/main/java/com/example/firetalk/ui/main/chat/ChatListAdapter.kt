package com.example.firetalk.ui.main.chat

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.firetalk.databinding.ItemChatBinding
import com.example.firetalk.model.Friend
import com.example.firetalk.model.Chat
import com.example.firetalk.ui.chatting.ChattingActivity
import com.example.firetalk.utils.UserPreferences
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.util.*
import kotlin.collections.ArrayList

class ChatListAdapter: RecyclerView.Adapter<ChatViewHolder>() {

    private var chatList = mutableListOf<Chat>()
    private var friendImage : String? = null
    private var friendName : String? = null
    private val fireDatabase = FirebaseDatabase.getInstance().reference

    private val friendUsers : ArrayList<String> = arrayListOf()

    fun  clearList(){
        chatList.clear()
    }
    fun setChatList(chat: Chat) {
        chatList.add(chat)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChatBinding.inflate(inflater, parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        (holder as? ChatViewHolder)?.onBind(chatList[position])
        var friendUid : String?=null
        for(user in chatList[position].users.keys){
            if(user != UserPreferences.id){
                friendUid = user
                friendUsers.add(friendUid)
            }
        }
        fireDatabase.child("users").child("$friendUid").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val friend = snapshot.getValue<Friend>()
                Glide.with(holder.itemView.context).load(friend?.image)
                    .apply(RequestOptions().circleCrop())
                    .into(holder.image_profile)
                holder.friend_name.text = friend?.name
            }
        })
        //채팅창으로 넘어가기
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, ChattingActivity::class.java)
            intent.putExtra("uid",friendUsers[position])
            it.context?.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}

class ChatViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {

    val image_profile : ImageView = binding.profileImage
    val friend_name : TextView = binding.name

    fun onBind(item: Chat){
        val commentMap = TreeMap<String, Chat.Comment>(reverseOrder())
        commentMap.putAll(item.comments)
        val lastMessageKey = commentMap.keys.toTypedArray()[0]
        binding.message.text = item.comments[lastMessageKey]?.message
        binding.timeStamp.text = item.comments[lastMessageKey]?.time
    }

}