package com.example.firetalk.ui.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.firetalk.databinding.FragmentMainHomeBinding

class HomeFragment : Fragment() {

    private var lBinding: FragmentMainHomeBinding? = null
    private val binding get() = lBinding!!

    private lateinit var viewModel: FriendViewModel
    private var adapter = FriendAdapter()

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        lBinding = FragmentMainHomeBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this)[FriendViewModel::class.java]

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLayout()
        viewModel.getFriend()
        observeViewModel()
    }
    private fun observeViewModel(){
        with(viewModel){
            myProfile.observe(viewLifecycleOwner){
                try{
                    binding.name.text = it[0].name
                    binding.email.text = it[0].email
                    Glide
                        .with(requireContext())
                        .load(it[0].image)
                        .apply(RequestOptions().circleCrop())
                        .into(binding.profileImage)
                }catch (e:Exception){
                    Toast.makeText(context,"잠시후 다시 이용해주세요.",Toast.LENGTH_SHORT).show()
                }
            }
            friendData.observe(viewLifecycleOwner){
                adapter.setFriendList(it)
            }
            errorData.observe(viewLifecycleOwner){
                Toast.makeText(context,"잠시후 다시 이용해주세요.",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun initLayout(){
        binding.recyclerView.adapter = adapter
    }
}