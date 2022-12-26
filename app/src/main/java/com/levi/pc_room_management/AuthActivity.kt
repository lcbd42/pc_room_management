package com.levi.pc_room_management

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class AuthActivity : AppCompatActivity() {

    //사용자 인증 저장
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val email = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)

        auth = Firebase.auth

        //사용자 인증 저장
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (isLoggedIn) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", true)
        editor.apply()


        val RegisterBtn = findViewById<Button>(R.id.login)
        RegisterBtn.setOnClickListener {

            auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                        when (email.text.toString()) {
                            "admin@naver.com" -> {
                                Toast.makeText(
                                    baseContext, "대표님께서 로그인 하셨습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            "amdin1@nate.com" -> {
                                Toast.makeText(
                                    baseContext, "실장님께서 로그인 하셨습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        Log.d("TAG", "signInWithEmail:success")


                    } else {
                        Log.w("TAG", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "E-Palace 관리자만 이용하실 수 있습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
