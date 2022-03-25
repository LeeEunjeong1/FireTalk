package com.example.firetalk.ui.main.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.firetalk.databinding.FragmentMainProfileBinding
import com.example.firetalk.model.Friend
import com.example.firetalk.model.User
import com.example.firetalk.ui.LoginActivity
import com.example.firetalk.utils.LoadingDialog
import com.example.firetalk.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var lBinding: FragmentMainProfileBinding? = null
    private val binding get() = lBinding!!

    private lateinit var viewModel: ProfileViewModel

    private lateinit var auth: FirebaseAuth
    private lateinit var fireDatabase : DatabaseReference
    private var imageUri : Uri? = null
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        lBinding = FragmentMainProfileBinding.inflate(inflater, container, false)

        auth = Firebase.auth
        fireDatabase = Firebase.database.reference

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        imageChange = false

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserProfile() //프로필 정보 불러오기
        observeProfile() //프로필 매핑
        initListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        lBinding = null
    }

    private fun observeProfile(){
        with(viewModel){
            profileData.observe(viewLifecycleOwner){
                try{
                    val profileList = profileData.value
                    binding.edtId.text = profileList?.get(0)?.email
                    binding.edtName.setText(profileList?.get(0)?.name)
                    Glide.with(requireContext())
                        .load(profileList?.get(0)?.image)
                        .apply(RequestOptions().circleCrop())
                        .into(binding.imageView)
                }catch (e:Exception){
                    Toast.makeText(context,"잠시 후  다시 시도해주세요.",Toast.LENGTH_SHORT).show()
                }
            }
            changeSuccess.observe(viewLifecycleOwner){
                if(it == true){
                    loadingDialog.dismiss()
                    Toast.makeText(context,"변경되었습니다.",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,"잠시 후  다시 시도해주세요.",Toast.LENGTH_SHORT).show()
                }

            }
        }
    }


    private fun initListener(){
        with(binding){
            //로그아웃 버튼
            btnLogout.setOnClickListener {
                UserPreferences.logout()
                viewModel.logout()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                activity?.finish()
            }
            //프로필사진 클릭했을때
            imageView.setOnClickListener {
                if(UserPreferences.google != "true"){
                    val intentImage = Intent(Intent.ACTION_PICK)
                    intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
                    getResult.launch(intentImage)
                }else{
                    Toast.makeText(context,"구글 로그인은 프로필을 변경할 수 없습니다.",Toast.LENGTH_SHORT).show()
                }
            }
            //edittext
            edtName.isEnabled = UserPreferences.google != "true"

            //저장하기 버튼
            btnProfile.setOnClickListener {
                if(UserPreferences.google != "true"){
                    if(edtName.text.isNotEmpty()){
                        //이름변경
                        viewModel.changeProfileName(edtName.text.toString())
                        edtName.clearFocus()
                        //프로필 사진 변경
                        if(imageChange){
                            loadingDialog.show()
                            viewModel.changeProfileImage(imageUri!!)
                        }else{
                            Toast.makeText(context,"변경되었습니다.",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context,"이름을 입력해주세요.",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context,"구글 로그인은 프로필을 변경할 수 없습니다.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private var getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == AppCompatActivity.RESULT_OK){
            // 이미지 원본 경로
            imageUri = it.data?.data
            // 이미지뷰  변경
            Glide.with(requireContext())
                .load(imageUri)
                .apply(
                    RequestOptions()
                        .circleCrop())
                .into(binding.imageView)
            imageChange = true
        }else{
            Toast.makeText(context,"다시 시도해주세요.",Toast.LENGTH_SHORT).show()
        }
    }
    companion object{
        var imageChange : Boolean =false
    }

}