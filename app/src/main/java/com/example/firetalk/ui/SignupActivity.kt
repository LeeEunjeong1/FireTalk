package com.example.firetalk.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.firetalk.databinding.ActivityLoginBinding
import com.example.firetalk.databinding.ActivitySignupBinding
import com.example.firetalk.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class SignupActivity :AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    private lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference

    private var imageUri : Uri?= null
    var pc: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)


        val loginIntent = Intent(this,LoginActivity::class.java)


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
                        auth.createUserWithEmailAndPassword(edtId.text.toString(), edtPwd.text.toString())
                            .addOnCompleteListener(this@SignupActivity){ task ->
                                if(task.isSuccessful){
                                    val user = Firebase.auth.currentUser
                                    val userId = user?.uid
                                    val userIdSt = userId.toString()

                                    FirebaseStorage.getInstance()
                                        .reference.child("userImage").child("$userIdSt/photo").putFile(imageUri!!)
                                        .addOnSuccessListener {
                                            var profile: Uri?
                                            FirebaseStorage.getInstance().reference.child("userImage").child("$userIdSt/photo").downloadUrl
                                                .addOnSuccessListener {
                                                    profile = it
                                                    Log.d("profile_check","$profile")
                                                    val user1 = User(edtId.text.toString(),edtName.text.toString(),profile.toString(),userIdSt)
                                                    Log.d("user1",user1.email)
                                                    database.child("users").child(userId.toString()).setValue(user1)
                                                }
                                        }
                                    Toast.makeText(applicationContext,"회원가입이 완료되었습니다.",Toast.LENGTH_SHORT).show()
                                    loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    startActivity(loginIntent)
                                }else{
                                    Toast.makeText(applicationContext,"회원가입에 실패했습니다.",Toast.LENGTH_SHORT).show()
                                }

                            }
                    }
                }
            }
        }
    }

    private var getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == AppCompatActivity.RESULT_OK){
            imageUri = it.data?.data // 이미지 원본 경로
            binding.profile.setImageURI(imageUri)
            pc = true
        }

    }
}