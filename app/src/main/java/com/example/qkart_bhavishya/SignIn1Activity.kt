package com.example.qkart_bhavishya

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
//import com.example.wocq_kart.databaseReference
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignIn1Activity : AppCompatActivity() {

    lateinit var databaseReference: DatabaseReference

    class SigninActivity : AppCompatActivity() {

        companion object {
            const val Key1 = "com.example.qkart_bhavishya.SigninActivity.username"
            const val Key2 = "com.example.qkart_bhavishya.SigninActivity.rollNo"
        }


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_sign_in1)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
            val signup = findViewById<TextView>(R.id.signup)
            signup.setOnClickListener {
                val signUpIntent = Intent(this, SignUp1Activity::class.java)
                startActivity(signUpIntent)
            }

            val signIn = findViewById<TextView>(R.id.signIn)
            val SIusername = findViewById<TextInputEditText>(R.id.SIusername)
            val SIpassword = findViewById<TextInputEditText>(R.id.SIpassword)

            signIn.setOnClickListener {
                val username = SIusername.text.toString().trim()
                val password = SIpassword.text.toString().trim()

                if (username.isNotEmpty() && password.isNotEmpty()) {
                    readData(username, password)
                } else {
                    Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        fun readData(username: String, password: String) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users")

            databaseReference.child(username).get().addOnSuccessListener {
                if (it.exists()) {
                    val storedPassword = it.child("password").value.toString()

                    if (storedPassword == password) {
                        val user = it.child("username").value
                        val rollNo = it.child("rollNo").value

                        val mainsignin = Intent(this, MainScreenActivity::class.java)
                        mainsignin.putExtra(Key1, user.toString())
                        mainsignin.putExtra(Key2, rollNo.toString())

                        startActivity(mainsignin)

                    } else {
                        Toast.makeText(
                            this,
                            "Username or Password is incorrect",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this, "Username or Password is incorrect", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Something went wrong during sign-in", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}