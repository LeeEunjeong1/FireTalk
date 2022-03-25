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

    private lateinit var auth: FirebaseAuth
    private lateinit var fireDatabase : DatabaseReference
    private var imageUri : Uri? = null
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        lBinding = FragmentMainProfileBinding.inflate(inflater, container, false)

        auth = Firebase.auth
        fireDatabase = Firebase.database.reference

        imageChange = false



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLayout()
        initListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        lBinding = null
    }

    private fun initLayout(){
        //프로필 값 넣기
        try{
            fireDatabase.child("users").child(UserPreferences.id).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) { }
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userProfile = snapshot.getValue<Friend>()
                    Glide.with(requireContext())
                        .load(userProfile?.image)
                        .apply(
                            RequestOptions()
                                .circleCrop())
                        .into(binding.imageView)
                    binding.edtId.text = userProfile?.email
                    binding.edtName.setText(userProfile?.name)
                }
            })
        }catch (e:Exception){
            Toast.makeText(context,"잠시 후  다시 시도해주세요.",Toast.LENGTH_SHORT).show()
        }


    }

    private fun initListener(){
        with(binding){
            //로그아웃 버튼
            btnLogout.setOnClickListener {
                UserPreferences.logout()
                auth.signOut()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
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
                        fireDatabase.child("users/${UserPreferences.id}/name").setValue(edtName.text.toString())
                        edtName.clearFocus()
                        //프로필 사진 변경
                        if(imageChange){
                            CoroutineScope(Dispatchers.Default).launch {
                                FirebaseStorage.getInstance().reference
                                    .child("userImage/${UserPreferences.id}/photo").delete().addOnSuccessListener {
                                        FirebaseStorage.getInstance().reference.child("userImage/${UserPreferences.id}/photo").putFile(imageUri!!).addOnSuccessListener {
                                            FirebaseStorage.getInstance().reference.child("userImage/${UserPreferences.id}/photo").downloadUrl.addOnSuccessListener {
                                                val photoUri : Uri = it
                                                Log.d("profileImage",it.toString())
                                                fireDatabase.child("users").child(UserPreferences.id).child("image").setValue(photoUri.toString())
                                                loadingDialog.dismiss()
                                                Toast.makeText(context,"변경되었습니다.",Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                            }.apply { loadingDialog.show() }
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