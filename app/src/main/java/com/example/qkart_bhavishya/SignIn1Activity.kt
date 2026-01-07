package com.example.qkart_bhavishya

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignIn1Activity : AppCompatActivity() {

    // 1. Declare Firebase Auth and Firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        const val Key1 = "com.example.qkart_bhavishya.SignIn1Activity.username"
        const val Key2 = "com.example.qkart_bhavishya.SignIn1Activity.rollNo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in1)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // --- AUTO-LOGIN CHECK START ---
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already logged in, fetch their role and redirect
            fetchUserData(currentUser.uid)
        }


        val signup = findViewById<TextView>(R.id.signup)
        val signIn = findViewById<TextView>(R.id.signIn)
        val SIemail = findViewById<TextInputEditText>(R.id.SIusername) // Treat this as Email now
        val SIpassword = findViewById<TextInputEditText>(R.id.SIpassword)

        signup.setOnClickListener {
            startActivity(Intent(this, SignUp1Activity::class.java))
            finish()
        }

        signIn.setOnClickListener {
            val email = SIemail.text.toString().trim()
            val password = SIpassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, pass: String) {
        // 3. Authenticate with Firebase Auth
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        fetchUserData(userId)
                    }
                } else {
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun fetchUserData(userId: String) {
        // 4. Retrieve profile data from Firestore using UID
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: ""
                    val rollNo = document.getString("rollNo") ?: ""
                    val role = document.getString("role") ?: "student"

                    // Save to SharedPreferences for offline use
                    val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("rollNo", rollNo)
                        putString("userName", name)
                        apply()
                    }

                    // 5. Role-based Redirection
                    if (role == "admin") {
                        val intentLiveOrders = Intent(this, LiveOrdersAdminActivity::class.java)
                        intent.putExtra(Key1, name)
                        intent.putExtra(Key2, rollNo)
                        startActivity(intentLiveOrders)
                    } else {
                        val intentMainScreen = Intent(this, MainScreenActivity::class.java)
                        intent.putExtra(Key1, name)
                        intent.putExtra(Key2, rollNo)
                        startActivity(intentMainScreen)
                    }
                    finish()
                } else {
                    Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}