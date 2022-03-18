package com.example.firetalk.ui

import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.firetalk.R
import com.example.firetalk.model.Friend
import com.example.firetalk.databinding.ItemMessageBinding
import com.example.firetalk.model.Chat
import com.example.firetalk.utils.UserPreferences
import com.google.firebase.database.DatabaseReference

class ChattingAdapter: RecyclerView.Adapter<ChattingViewHolder>() {
    private lateinit var fireDatabase : DatabaseReference
    private var comments = mutableListOf<Chat.Comment>()
    private var friend: Friend? = null
    private var friendImage : String? = null
    private var friendName : String? = null

    fun  clearList(){
        comments.clear()
    }
    fun setChattingList(chat: Chat.Comment) {
        comments.add(chat)
        notifyDataSetChanged()
    }
    fun setFriend(friendImg : String,friendNa: String){
        friendImage = friendImg
        friendName = friendNa
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChattingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMessageBinding.inflate(inflater, parent, false)
        return ChattingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChattingViewHolder, position: Int) {
        (holder as? ChattingViewHolder)?.onBind(comments[position])
        Glide.with(holder.itemView.context)
            .load(friendImage)
            .apply(RequestOptions().circleCrop())
            .into(holder.image_profile)
        holder.friend_name.text = friendName
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}

class ChattingViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
    val image_profile : ImageView = binding.profileImage
    val friend_name : TextView = binding.name

    @RequiresApi(Build.VERSION_CODES.M)
    fun onBind(item: Chat.Comment){
        with(binding){
            message.textSize = 20F
            message.text = item.message
            timeStamp.text = item.time
            //본인 채팅
            if(item.uid.equals(UserPreferences.id)){
                message.setBackgroundResource(R.drawable.my_bubble)
                name.visibility = View.INVISIBLE
                profileImage.visibility = View.INVISIBLE

                layoutMessage.gravity = Gravity.END
            }else{
                layoutMessage.visibility = View.VISIBLE
                name.visibility = View.VISIBLE
                message.setBackgroundResource(R.drawable.friend_bubble)
                layoutMessage.gravity = Gravity.START
            }
        }
    }

}

