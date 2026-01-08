package com.example.qkart_bhavishya

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ActivityCart : AppCompatActivity() {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnPlaceOrder: AppCompatButton
    private lateinit var tvEmptyCartMsg: LinearLayout
    private lateinit var cartAdapter: CartAdapter
    private lateinit var bottomNav: BottomNavigationView

    private var selectedPickupTime: String = "ASAP"
    private val helper = FirestoreHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // 1. Initialize Views
        rvCart = findViewById(R.id.rvCartItems)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        tvEmptyCartMsg = findViewById(R.id.tvEmptyCartMsg)
        bottomNav = findViewById(R.id.bottomNavigationView)

        setupRecyclerView()
        updateUI()

        // 2. Setup Bottom Nav
        setupBottomNav()

        // 3. Place Order Button Logic
        btnPlaceOrder.setOnClickListener {
            val items = CartManager.getCartList()
            if (items.isNotEmpty()) {
                placeFinalOrder(items)
            } else {
                Toast.makeText(this, "Add items to your cart first!", Toast.LENGTH_SHORT).show()
            }
        }

        val tvSelectedTime = findViewById<TextView>(R.id.tvSelectedTime)
        val btnSelectTime = findViewById<TextView>(R.id.btnSelectTime)

        btnSelectTime.setOnClickListener {
            val cal = java.util.Calendar.getInstance()
            val timeSetListener = android.app.TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(java.util.Calendar.HOUR_OF_DAY, hour)
                cal.set(java.util.Calendar.MINUTE, minute)

                // Format time nicely
                val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                selectedPickupTime = sdf.format(cal.time)
                tvSelectedTime.text = selectedPickupTime
            }

            android.app.TimePickerDialog(
                this, timeSetListener,
                cal.get(java.util.Calendar.HOUR_OF_DAY),
                cal.get(java.util.Calendar.MINUTE),
                false // 'false' for 12-hour format with AM/PM
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Highlight the 'Cart' tab when this activity is visible
        bottomNav.selectedItemId = R.id.nav_cart
    }

    private fun setupBottomNav() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainScreenActivity::class.java))
                    finish() // Close cart so we don't stack
                    true
                }
                R.id.nav_cart -> {
                    // Already here
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, OrderHistoryActivity::class.java))
                    // finish() // Optional: keep cart in backstack or not
                    true
                }
                R.id.nav_account -> {
                    startActivity(Intent(this, AccountActivity::class.java))
                    // finish() // Optional
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        rvCart.layoutManager = LinearLayoutManager(this)

        // Whenever + or - is clicked in the adapter, this block runs
        cartAdapter = CartAdapter(CartManager.getCartList()) {
            updateUI() // Recalculate total and check if empty
        }

        rvCart.adapter = cartAdapter
    }

    private fun updateUI() {
        val total = CartManager.getTotalPrice()
        tvTotalAmount.text = "₹ $total"

        // Handle the "Empty State"
        if (CartManager.getCartList().isEmpty()) {
            tvEmptyCartMsg.visibility = View.VISIBLE
            rvCart.visibility = View.GONE
            btnPlaceOrder.isEnabled = false
            btnPlaceOrder.alpha = 0.5f // Makes button look disabled
        } else {
            tvEmptyCartMsg.visibility = View.GONE
            rvCart.visibility = View.VISIBLE
            btnPlaceOrder.isEnabled = true
            btnPlaceOrder.alpha = 1.0f
        }

        // Refresh the list items in the adapter
        cartAdapter.updateData(CartManager.getCartList())
    }

    private fun placeFinalOrder(items: List<CartItem>) {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val rollNo = sharedPref.getString("rollNo", "Unknown") ?: "Unknown"
        val name = sharedPref.getString("userName", "Student") ?: "Student"

        val newOrder = OrderModel(
            username = name,
            rollNo = rollNo,
            items = items,
            totalAmount = CartManager.getTotalPrice(),
            status = "Pending",
            pickupTime = selectedPickupTime,
            timestamp = System.currentTimeMillis()
        )

        helper.placeOrder(newOrder) { success ->
            if (success) {
                Toast.makeText(this, "Order Sent to Canteen!", Toast.LENGTH_LONG).show()
                CartManager.clearCart()
                finish() // Close the cart activity
            } else {
                Toast.makeText(this, "Failed to send order. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}