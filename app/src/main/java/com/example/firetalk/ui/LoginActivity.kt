package com.example.firetalk.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.firetalk.R
import com.example.firetalk.databinding.ActivityLoginBinding
import com.example.firetalk.model.User
import com.example.firetalk.ui.main.MainActivity
import com.example.firetalk.utils.UserPreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception

class LoginActivity :AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var client: GoogleSignInClient
    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //firebase.auth
        auth = Firebase.auth
        database = Firebase.database.reference

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
            btnGoogleLogin.setOnClickListener{
                doLoginGoogle()
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

    /*구글 로그인*/

    private fun doLoginGoogle(){
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        client = GoogleSignIn.getClient(this,options)

        val intentGoogleLogin = Intent(client.signInIntent)
        getResult.launch(intentGoogleLogin)

    }
    private var getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == AppCompatActivity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            var account:  GoogleSignInAccount? = null
            try{
                account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!.idToken)
                Log.d("ioToken",account.email!!)
            }catch (e: ApiException){
                e.printStackTrace()
                Toast.makeText(this,"다시 시도해주세요",Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun firebaseAuthWithGoogle(idToken: String?) {
        Log.d("here",idToken!!)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this,
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        // 인증에 성공한 후, 현재 로그인된 유저의 정보를 가져올 수 있습니다.
                        val uid = auth.currentUser?.uid!!
                        val id = auth.currentUser?.email!!
                        val name = auth.currentUser?.displayName!!
                        val profile = auth.currentUser?.photoUrl!!
                        Log.d("uid_check",uid)
                        Log.d("uid_check",id)

                        val user1 = User(id,name,profile.toString(),uid)
                        database.child("users").child(uid).setValue(user1)
                        UserPreferences.id = uid
                        UserPreferences.google = "true"

                        val mainIntent = Intent(this,MainActivity::class.java)
                        Toast.makeText(applicationContext,"로그인되었습니다.",Toast.LENGTH_SHORT).show()
                        mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(mainIntent)
                        finish()

                    }
                })
    }
}