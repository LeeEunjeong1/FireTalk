package com.example.firetalk.ui.signup

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firetalk.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupViewModel:ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth
    private var database: DatabaseReference = Firebase.database.reference

    var isSuccess1 = MutableLiveData<Boolean>()
    val isSuccess get() = isSuccess1

    fun doSignUp(edtId:String, edtPwd:String,edtName:String, activity: Activity, imageUri: Uri) {
        CoroutineScope(Dispatchers.Default).launch {
            try{
                auth.createUserWithEmailAndPassword(edtId, edtPwd)
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            val user = Firebase.auth.currentUser
                            val userId = user?.uid
                            val userIdSt = userId.toString()

                            FirebaseStorage.getInstance()
                                .reference.child("userImage").child("$userIdSt/photo").putFile(imageUri)
                                .addOnSuccessListener {
                                    var profile: Uri?
                                    FirebaseStorage.getInstance().reference.child("userImage")
                                        .child("$userIdSt/photo").downloadUrl
                                        .addOnSuccessListener {
                                            profile = it
                                            Log.d("profile_check", "$profile")
                                            val user1 = User(edtId, edtName, profile.toString(),)
                                            Log.d("user1", user1.email)
                                            database.child("users").child(userId.toString()).setValue(user1)
                                        }
                                }
                            isSuccess1.postValue(true)
                        } else {
                            isSuccess1.postValue(false)
                        }
                    }
            }catch (e:Exception){
                isSuccess1.postValue(false)
            }
        }

    }
}