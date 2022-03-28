package com.example.firetalk.ui.login

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firetalk.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth

    private var isLoginSuccess1 = MutableLiveData<Boolean>()
    val isLoginSuccess get() = isLoginSuccess1

    var error1 = MutableLiveData<String>()
    val error get() = error1

    fun doLogin(id: String, pwd : String, activity:Activity){
        CoroutineScope(Dispatchers.Default).launch {
            try{
                auth.signInWithEmailAndPassword(id,pwd)
                    .addOnCompleteListener(activity){ task->
                        if(task.isSuccessful){
                            UserPreferences.id = auth.currentUser!!.uid
                            isLoginSuccess1.postValue(true)
                        }else{
                            UserPreferences.id = ""
                            isLoginSuccess1.postValue(false)
                        }
                    }
            }catch (e:Exception){
                error1.postValue(e.message)
            }
        }

    }


}