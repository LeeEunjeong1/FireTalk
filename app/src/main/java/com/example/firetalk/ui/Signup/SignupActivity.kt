package com.example.firetalk.ui.signup

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.firetalk.databinding.ActivitySignupBinding
import com.example.firetalk.ui.login.LoginActivity

class SignupActivity :AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel : SignupViewModel

    private var imageUri : Uri?= null
    var pc: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SignupViewModel::class.java]

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        observeViewModel()
        initBinding()
    }

    private fun initBinding(){
        with(binding){
            profile.setOnClickListener{
                val intentImage = Intent(Intent.ACTION_PICK)
                intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
                getResult.launch(intentImage)
            }
            btnSignup.setOnClickListener{
                if(edtId.text.isEmpty() || edtPwd.text.isEmpty() || edtName.text.isEmpty()){
                    Toast.makeText(applicationContext,"모두 다 입력해주세요",Toast.LENGTH_SHORT).show()
                }else{
                    if(!pc){
                        Toast.makeText(applicationContext,"프로필  사진을 등록해주세요.",Toast.LENGTH_SHORT).show()
                    }else{
                        viewModel.doSignUp(edtId.text.toString(),edtPwd.text.toString(),edtName.text.toString(),this@SignupActivity,imageUri!!)
                    }
                }
            }
        }
    }

    private  fun observeViewModel(){
        val loginIntent = Intent(this, LoginActivity::class.java)
        with(viewModel){
            isSuccess.observe(this@SignupActivity){
                if(it == true){
                    Toast.makeText(applicationContext,"회원가입이 완료되었습니다.",Toast.LENGTH_SHORT).show()
                    loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(loginIntent)
                }else{
                    Toast.makeText(applicationContext,"회원가입에 실패했습니다.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private var getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == AppCompatActivity.RESULT_OK){
            imageUri = it.data?.data // 이미지 원본 경로
            Glide.with(applicationContext)
                .load(imageUri)
                .apply(
                    RequestOptions()
                        .circleCrop())
                .into(binding.profile)
            pc = true
        }

    }
}