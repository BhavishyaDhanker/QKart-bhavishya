package com.example.qkart_bhavishya

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class AccountActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvRoll: TextView
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        tvName = findViewById(R.id.tvAccountName)
        tvRoll = findViewById(R.id.tvAccountRoll)
        bottomNav = findViewById(R.id.bottomNavigationView)

        loadUserData()

        // 1. Setup Card Buttons

        // NEW: Edit Profile Listener
        findViewById<TextView>(R.id.btnAccountEditProfile).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        findViewById<TextView>(R.id.btnAccountHistory).setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }

        findViewById<TextView>(R.id.btnAccountLogout).setOnClickListener {
            showLogoutDialog()
        }

        // 2. Setup Bottom Nav
        setupBottomNav()

        // 3. Initialize Badge
        updateCartBadge()
    }

    override fun onResume() {
        super.onResume()
        // Highlight the 'Account' tab when this activity is visible
        bottomNav.selectedItemId = R.id.nav_account
        updateCartBadge()

        // Refresh Name/Roll in case they were edited in EditProfileActivity
        loadUserData()
    }

    private fun setupBottomNav() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainScreenActivity::class.java))
                    finish() // Close account so we don't stack activities
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, ActivityCart::class.java))
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, OrderHistoryActivity::class.java))
                    true
                }
                R.id.nav_account -> {
                    // Already here
                    true
                }
                else -> false
            }
        }
    }

    private fun updateCartBadge() {
        val cartSize = CartManager.getCartSize()
        val badge = bottomNav.getOrCreateBadge(R.id.nav_cart)
        if (cartSize > 0) {
            badge.isVisible = true
            badge.number = cartSize
            badge.backgroundColor = getColor(R.color.white)
            badge.badgeTextColor = getColor(R.color.maroon)
        } else {
            badge.isVisible = false
        }
    }

    private fun loadUserData() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        tvName.text = sharedPref.getString("userName", "Student")
        tvRoll.text = "Roll No: ${sharedPref.getString("rollNo", "Unknown")}"
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to log out?")
        builder.setPositiveButton("Yes") { _, _ ->
            FirebaseAuth.getInstance().signOut()
            getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit().clear().apply()
            CartManager.clearCart()

            val intent = Intent(this, SignIn1Activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }
}