package com.example.qkart_bhavishya

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SignUp1Activity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up1)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        if (auth.currentUser != null) {
            //---Auto Login code---
            val userId = auth.currentUser!!.uid
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(userId).get().addOnSuccessListener { doc ->
                val role = doc.getString("role") ?: "student"
                val intent = if (role == "admin") {
                    Intent(this, LiveOrdersAdminActivity::class.java)
                } else {
                    Intent(this, MainScreenActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
            return
        }


        val signUpBtn = findViewById<TextView>(R.id.signUp)
        val signinLink = findViewById<TextView>(R.id.signin)

        val etName = findViewById<TextInputEditText>(R.id.SUusername)
        val etEmail = findViewById<TextInputEditText>(R.id.SUemail)
        val etRollNo = findViewById<TextInputEditText>(R.id.SUroll)
        val etPass = findViewById<TextInputEditText>(R.id.SUpass)

        signinLink.setOnClickListener {
            startActivity(Intent(this, SignIn1Activity::class.java))
            finish()
        }

        signUpBtn.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val rollNo = etRollNo.text.toString().trim()
            val pass = etPass.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && rollNo.isNotEmpty() && pass.length >= 6) {
                registerUser(email, pass, name, rollNo)
            } else {
                Toast.makeText(this, "Please fill all fields. Password min 6 chars.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, pass: String, name: String, rollNo: String) {
        // 1. Create the user in Firebase Auth
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid

                    // 2. Prepare user profile data
                    val userMap = hashMapOf(
                        "uid" to userId,
                        "name" to name,
                        "email" to email,
                        "rollNo" to rollNo,
                        "role" to "student", // Hardcoded as requested
                        "createdAt" to System.currentTimeMillis()
                    )

                    // 3. Save to Firestore
                    if (userId != null) {
                        db.collection("users").document(userId)
                            .set(userMap, SetOptions.merge())
                            .addOnSuccessListener {
                                Toast.makeText(this, "Welcome, $name!", Toast.LENGTH_SHORT).show()

                                // Navigate to Main Screen
                                val intent = Intent(this, MainScreenActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Database Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Auth Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}