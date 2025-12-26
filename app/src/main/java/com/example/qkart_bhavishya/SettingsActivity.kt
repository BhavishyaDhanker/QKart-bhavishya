package com.example.qkart_bhavishya

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
    }
}