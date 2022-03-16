package com.example.firetalk.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firetalk.databinding.FragmentMainHomeBinding
import com.example.firetalk.model.Friend
import com.example.firetalk.utils.UserPreferences
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var lBinding: FragmentMainHomeBinding? = null
    private val binding get() = lBinding!!

    private lateinit var database : DatabaseReference
    private var adapter = FriendAdapter()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        lBinding = FragmentMainHomeBinding.inflate(inflater, container, false)


        initLayout()
        getFriend()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        lBinding = null
    }

    private fun initLayout(){
        binding.recyclerView.adapter = adapter
    }
    private fun getFriend(){
        database = Firebase.database.reference
      //  val myUid = Firebase.auth.currentUser?.uid.toString()
        val myUid = UserPreferences.id
        FirebaseDatabase.getInstance().reference.child("users").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                adapter.clearList()
                for(data in snapshot.children){
                    val item = data.getValue<Friend>()
                    if(item?.uid.equals(myUid)){
                        continue
                    }
                    adapter.setFriendList(item!!)
                }

            }
        })
    }

}