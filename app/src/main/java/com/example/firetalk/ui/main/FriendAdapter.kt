package com.example.firetalk.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firetalk.model.Friend
import com.example.firetalk.databinding.ItemFriendBinding

class FriendAdapter: RecyclerView.Adapter<FriendViewHolder>() {

    private var friendList = mutableListOf<Friend>()
    fun  clearList(){
        friendList.clear()
    }
    fun setFriendList(friend: Friend) {
        friendList.add(friend)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = ItemFriendBinding.inflate(inflater, parent, false)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friendList = friendList[position]
        holder.binding.name.text = friendList.name
        holder.binding.email.text = friendList.email
        Glide.with(holder.itemView.context).load(friendList.image).into(holder.binding.profileImage)
        Log.d("profile : ",friendList.image.toString())

    }

    override fun getItemCount(): Int {
        return friendList.size
    }
}

class FriendViewHolder(val binding: ItemFriendBinding) : RecyclerView.ViewHolder(binding.root) {

}