package com.example.firetalk.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firetalk.databinding.ActivityLoginBinding
import com.example.firetalk.ui.main.MainActivity
import com.example.firetalk.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity :AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //firebase.auth
        auth = Firebase.auth

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBinding()
    }

    private fun initBinding(){
        val signupIntent = Intent(this,SignupActivity::class.java)
        with(binding){
            btnLogin.setOnClickListener{
                if(edtId.text.isEmpty() && edtPwd.text.isEmpty()){
                    Toast.makeText(applicationContext,"아이디 또는 비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show()
                }else{
                    doLogin(edtId.text.toString(), edtPwd.text.toString())
                }
            }
            btnSignup.setOnClickListener {

                startActivity(signupIntent)
                Log.d("here","here")
            }

        }
    }

    private fun doLogin(id: String, pwd : String){
        val intentMain = Intent(this, MainActivity::class.java)

        auth.signInWithEmailAndPassword(id,pwd)
            .addOnCompleteListener(this){task->
                if(task.isSuccessful){
                    Log.d("Login",task.toString())
                    UserPreferences.id = auth.currentUser!!.uid
                    finish()
                    startActivity(intentMain)
                }else{
                    Toast.makeText(applicationContext,"아이디 또는 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show()
                    Log.d("Login 실패",task.toString())
                    UserPreferences.id = ""
                }
            }
    }
    public override fun onStart(){
        super.onStart()
        val user = auth.currentUser
        Log.d("LoginActivity_onStart ",user.toString())
        if(user != null){
            reload()
        }
    }
    private fun reload(){

    }
}