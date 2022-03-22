package com.example.firetalk.ui.main.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.firetalk.databinding.FragmentMainHomeBinding
import com.example.firetalk.model.Friend
import com.example.firetalk.utils.UserPreferences
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class HomeFragment : Fragment() {

    private var lBinding: FragmentMainHomeBinding? = null
    private val binding get() = lBinding!!

    private lateinit var database : DatabaseReference
    private var adapter = FriendAdapter()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        lBinding = FragmentMainHomeBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLayout()
        getFriend()
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
                Toast.makeText(requireContext(),"error",Toast.LENGTH_SHORT).show()
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                adapter.clearList()
                for(data in snapshot.children){
                    val item = data.getValue<Friend>()
                    //내 프로필
                    if(item?.uid.equals(myUid)){
                        try{
                            if(context != null){
                                binding.name.text = item?.name
                                binding.email.text = item?.email
                                Glide
                                    .with(requireContext())
                                    .load(item?.image)
                                    .apply(RequestOptions().circleCrop())
                                    .into(binding.profileImage)
                            }

                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                        continue
                    }
                    adapter.setFriendList(item!!)
                }

            }
        })
    }

}