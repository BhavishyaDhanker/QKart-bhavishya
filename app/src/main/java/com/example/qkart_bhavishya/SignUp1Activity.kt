package com.example.qkart_bhavishya

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wocq_kart.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp1Activity : AppCompatActivity() {

    lateinit var database: DatabaseReference

    companion object {
        const val Key1 = "com.example.qkart_bhavishya.SignUp1Activity.username"
        const val Key2 = "com.example.qkart_bhavishya.SignUp1Activity.rollNo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val signUp = findViewById<TextView>(R.id.signUp)
        val signin = findViewById<TextView>(R.id.signin)
        val SUusername = findViewById<TextInputEditText>(R.id.SUusername)
        val SUroll = findViewById<TextInputEditText>(R.id.SUroll)
        val SUpass = findViewById<TextInputEditText>(R.id.SUpass)

        signin.setOnClickListener {
            val signInIntent = Intent(this, SignIn1Activity::class.java)
            startActivity(signInIntent)
            finish()
        }

        signUp.setOnClickListener {
            val username = SUusername.text.toString()
            val rollNo = SUroll.text.toString()
            val pass = SUpass.text.toString()

            val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("rollNo", rollNo)
                putString("userName", username)
                apply()
            }

            if (username.isNotEmpty() && rollNo.isNotEmpty() && pass.isNotEmpty()) {
                val user = User(username, rollNo, pass, "student")
                database = FirebaseDatabase.getInstance().getReference("Users")

                database.child(username).setValue(user).addOnSuccessListener {
                    Toast.makeText(this, "User Registered", Toast.LENGTH_SHORT).show()
                    SUusername.text?.clear()
                    SUroll.text?.clear()
                    SUpass.text?.clear()

                    val mainsignup = Intent(this, MainScreenActivity::class.java)
                    mainsignup.putExtra(Key1, username)
                    mainsignup.putExtra(Key2, rollNo)
                    startActivity(mainsignup)
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Error user not registered", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}