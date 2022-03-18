package com.example.firetalk.ui.main.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firetalk.databinding.FragmentMainChatBinding
import com.example.firetalk.model.Chat
import com.example.firetalk.ui.main.home.ChatListAdapter
import com.example.firetalk.utils.UserPreferences
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class ChatFragment : Fragment() {

    private var lBinding: FragmentMainChatBinding? = null
    private val binding get() = lBinding!!

    private var adapter = ChatListAdapter()

    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private val chatModel = ArrayList<Chat>()
    private var myUid : String? = null
    private val friends : ArrayList<String> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        lBinding = FragmentMainChatBinding.inflate(inflater, container, false)
        binding.recyclerView.adapter = adapter

        getChatRoom()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        lBinding = null
    }

    private fun getChatRoom(){
        myUid = UserPreferences.id
        fireDatabase.child("chatrooms").orderByChild("users/$myUid").equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                adapter.clearList()
                for(data in snapshot.children){
                    val item = data.getValue<Chat>()
                    adapter.setChatList(item!!)
                }
            }
        })
    }

}