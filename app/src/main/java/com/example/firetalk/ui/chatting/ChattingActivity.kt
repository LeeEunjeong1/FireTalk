package com.example.firetalk.ui.chatting

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.firetalk.databinding.ActivityChattingBinding

class ChattingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChattingBinding
    private var adapter = ChattingAdapter()

    private lateinit var viewModel: ChattingViewModel

    private var friendUid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChattingBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ChattingViewModel::class.java]
        binding.recyclerView.adapter = adapter

        friendUid = intent.getStringExtra("uid")

        initListener()
        initViewModel()
        observeViewModel()

        setContentView(binding.root)
    }

    private fun initViewModel(){
        with(viewModel){
            getFriendName(friendUid!!)
            checkChatRoom(friendUid!!)
        }
    }

    private fun initListener(){
        with(binding){
            imageView.setOnClickListener{
                viewModel.doChatting(friendUid!!,sendMsg.text.toString())
            }
        }
    }
    private fun observeViewModel(){
        with(viewModel){
            friendName.observe(this@ChattingActivity){
                try{
                    binding.name.text = it
                }catch (e:Exception){
                    Toast.makeText(applicationContext,"다시 한 번 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            chattingMessage.observe(this@ChattingActivity){
                adapter.clearList()
                adapter.setChattingList(it!!)
            }
            friendInfo.observe(this@ChattingActivity){
                adapter.setFriend(it[0],it[1])
                binding.recyclerView.scrollToPosition(adapter.itemCount-1)
            }
            error.observe(this@ChattingActivity){
                Toast.makeText(applicationContext,"다시 한 번 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
            isSuccess.observe(this@ChattingActivity){
                if(it == true){
                    binding.sendMsg.text = null
                }
            }
        }
    }

}