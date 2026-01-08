package com.example.qkart_bhavishya

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AccountActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvRoll: TextView
    private lateinit var cartBadgeDot: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        tvName = findViewById(R.id.tvAccountName)
        tvRoll = findViewById(R.id.tvAccountRoll)
        cartBadgeDot = findViewById(R.id.cartBadgeDot)

        // 1. Load User Data
        loadUserData()

        // 2. Setup Menu Buttons (Inside the Card)
        // Note: Edit Profile was removed from your XML, so I removed the listener.

        findViewById<TextView>(R.id.btnAccountHistory).setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }

        findViewById<TextView>(R.id.btnAccountLogout).setOnClickListener {
            showLogoutDialog()
        }

        // 3. Setup Bottom Navigation Bar
        setupBottomNav()
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge() // Refresh badge when returning to this screen
    }

    private fun loadUserData() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val name = sharedPref.getString("userName", "Student")
        val roll = sharedPref.getString("rollNo", "Unknown Roll No")

        tvName.text = name
        tvRoll.text = "Roll No: $roll"
    }

    private fun updateCartBadge() {
        // Checks if Cart has items and shows/hides the red dot
        if (CartManager.getCartSize() > 0) {
            cartBadgeDot.visibility = View.VISIBLE
        } else {
            cartBadgeDot.visibility = View.GONE
        }
    }

    private fun setupBottomNav() {

        findViewById<ImageView>(R.id.home).setOnClickListener {
            val intent = Intent(this, MainScreenActivity::class.java)
            // Clearing back stack so user can't press back to return to Account
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }


        findViewById<View>(R.id.cartContainer).setOnClickListener {
            startActivity(Intent(this, ActivityCart::class.java))
        }


        findViewById<ImageView>(R.id.OrderHistory).setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }

        findViewById<ImageView>(R.id.settings).setOnClickListener {
            startActivity(Intent(this , SettingsActivity::class.java))
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to log out?")
        builder.setIcon(R.drawable.outline_exit_24) // Ensure you have this drawable

        builder.setPositiveButton("Yes") { _, _ ->
            // Clear Firebase Auth
            FirebaseAuth.getInstance().signOut()

            // Clear Local Data
            val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            // Clear Cart
            CartManager.clearCart()

            // Go to Login
            val intent = Intent(this, SignIn1Activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }
}