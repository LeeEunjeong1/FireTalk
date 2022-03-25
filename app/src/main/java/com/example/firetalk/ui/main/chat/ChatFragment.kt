package com.example.firetalk.ui.main.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.firetalk.databinding.FragmentMainChatBinding
class ChatFragment : Fragment() {

    private var lBinding: FragmentMainChatBinding? = null
    private val binding get() = lBinding!!

    private lateinit var viewModel: ChatViewModel

    private var adapter = ChatListAdapter()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        lBinding = FragmentMainChatBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = adapter
        viewModel.getChat()
        observeViewModel()

    }

    override fun onDestroy() {
        super.onDestroy()
        lBinding = null
    }

    private fun observeViewModel(){
        with(viewModel){
            chatList.observe(viewLifecycleOwner){
                adapter.setChatList(it)
            }
            errorData.observe(viewLifecycleOwner){
                Toast.makeText(requireContext(),"다시 한 번 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}