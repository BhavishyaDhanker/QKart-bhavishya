package com.example.qkart_bhavishya

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val rollNo = sharedPref.getString("rollNo", "Unknown") ?: "Unknown"
        val name = sharedPref.getString("userName", "Student") ?: "Student"


        val setMyRollNo = findViewById<TextView>(R.id.setMyRollNo)
        setMyRollNo.setText("Roll Number: $rollNo")

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intentback = Intent(this, MainScreenActivity::class.java)
            startActivity(intentback)
            finish()
        }
            val btnLogout = findViewById<TextView>(R.id.setLogout)

            btnLogout.setOnClickListener {

                val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                builder.setTitle("Logout")
                builder.setMessage("Are you sure you want to log out?")
                builder.setIcon(R.drawable.outline_exit_24)


                builder.setPositiveButton("Yes") { dialog, _ ->
                    performLogout()
                    dialog.dismiss()
                }


                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }


                val alertDialog = builder.create()
                alertDialog.show()
            }
    }

    private fun performLogout() {
        // 1. Sign out from Firebase Auth
        FirebaseAuth.getInstance().signOut()

        // 2. Clear SharedPreferences
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        // 3. Navigate to SignIn screen and clear activity stack
        val intent = Intent(this, SignIn1Activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}