package com.example.firetalk.ui.main.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.firetalk.model.Friend
import com.example.firetalk.databinding.ItemFriendBinding
import com.example.firetalk.ui.chatting.ChattingActivity

class FriendAdapter: RecyclerView.Adapter<FriendViewHolder>() {

    private var friendList = mutableListOf<Friend>()

    fun  clearList(){
        friendList.clear()
    }

    fun setFriendList(friend: List<Friend>) {
        friendList.addAll(friend)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFriendBinding.inflate(inflater, parent, false)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        (holder as? FriendViewHolder)?.onBind(friendList[position])
    }

    override fun getItemCount(): Int {
        return friendList.size
    }
}

class FriendViewHolder(private val binding: ItemFriendBinding) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(item: Friend){
        with(binding){
            name.text = item.name
            email.text = item.email
            Glide.with(itemView.context)
                .load(item.image)
                .apply(RequestOptions().circleCrop())
                .into(profileImage)
        }
        //친구 클릭시 채팅창으로 넘어가기
        itemView.setOnClickListener {
            val intent = Intent(it.context, ChattingActivity::class.java)

            intent.putExtra("uid",item.uid)
            it.context.startActivity(intent)
        }
    }

}