package com.example.loginwithfirebase

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.loginwithfirebase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var authF : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authF = FirebaseAuth.getInstance()
        //authF.signOut()
        btnClick()
    }

    override fun onStart() {
        super.onStart()
        checkLogState()
    }





    private fun btnClick(){
        binding.btnRegister.setOnClickListener {
            register()
        }
        binding.btnLogin.setOnClickListener {
            login()
        }
        binding.btnUpdate.setOnClickListener {
            updateProfile()
        }

    }



    private fun register(){
        val email = binding.edtRegisterEmail.text.toString()
        val pass = binding.edtRegisterPass.text.toString()

        if(email.isNotEmpty() && pass.isNotEmpty()){
            lifecycleScope.launch(Dispatchers.IO) {
                try{
                    authF.createUserWithEmailAndPassword(email,pass).await()
                    withContext(Dispatchers.Main){
                        checkLogState()
                    }

                } catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }







    private fun login() {
        val email = binding.edtLoginEmail.text.toString()
        val pass = binding.edtLoginPass.text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    authF.signInWithEmailAndPassword(email, pass).await()
                    withContext(Dispatchers.Main) {
                        checkLogState()
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }



        private fun updateProfile(){
            val user = authF.currentUser
            authF?.currentUser.let { user->
                val userName = binding.edtUserName.text.toString()
                //shown below : what is the format to apply for uri
                val photoUri = Uri.parse("android.resource://$packageName/${R.drawable.ic_launcher_foreground}")
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(userName)
                    .setPhotoUri(photoUri)
                    .build()


                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        user?.updateProfile(profileUpdates)?.await()
                        withContext(Dispatchers.Main){
                            checkLogState()
                            Toast.makeText(this@MainActivity,"Successfully updated",Toast.LENGTH_LONG).show()
                        }
                    } catch (e:Exception){
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                        }
                    }
                }

            }
        }






    private fun checkLogState(){

        val user = authF.currentUser
        if (user != null){
            binding.txtInfo.text = "You are logged in"
            binding.edtUserName.setText(user.displayName)
            binding.imgProfile.setImageURI(user.photoUrl)
        }else{
            binding.txtInfo.text = "You are not logged in"
        }
    }


}