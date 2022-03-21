package com.example.firetalk.ui.main.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firetalk.databinding.FragmentMainProfileBinding
import com.example.firetalk.ui.LoginActivity
import com.example.firetalk.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private var lBinding: FragmentMainProfileBinding? = null
    private val binding get() = lBinding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        lBinding = FragmentMainProfileBinding.inflate(inflater, container, false)

        auth = Firebase.auth
        initListener()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        lBinding = null
    }

    private fun initListener(){
        with(binding){
            btnLogout.setOnClickListener {
                UserPreferences.logout()
                auth.signOut()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                activity?.finish()

            }
        }
    }

}