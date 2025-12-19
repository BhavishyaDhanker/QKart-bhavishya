package com.example.qkart_bhavishya

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignIn1Activity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference

    companion object {
        const val Key1 = "com.example.qkart_bhavishya.SignIn1Activity.username"
        const val Key2 = "com.example.qkart_bhavishya.SignIn1Activity.rollNo"
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

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        val signup = findViewById<TextView>(R.id.signup)
        signup.setOnClickListener {
            startActivity(Intent(this, SignUp1Activity::class.java))
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
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readData(username: String, password: String) {
        databaseReference.child(username).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val storedPassword = it.child("password").value.toString()

                    if (storedPassword == password) {
                        val user = it.child("username").value.toString()
                        val rollNo = it.child("rollNo").value.toString()

                        val role = it.child("role").value?.toString() ?: "student"

                        if (role == "admin") {
                            val intent = Intent(this, LiveOrdersAdminActivity::class.java)
                            intent.putExtra(Key1, user)
                            intent.putExtra(Key2, rollNo)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this, MainScreenActivity::class.java)
                            intent.putExtra(Key1, user)
                            intent.putExtra(Key2, rollNo)
                            startActivity(intent)
                        }
                        finish()

                    } else {
                        Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show()
            }
    }
}