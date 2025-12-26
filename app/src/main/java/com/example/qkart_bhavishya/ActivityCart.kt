package com.example.qkart_bhavishya

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ActivityCart : AppCompatActivity() {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnPlaceOrder: AppCompatButton
    private lateinit var tvEmptyCartMsg: LinearLayout
    private lateinit var cartAdapter: CartAdapter
    private val helper = FirestoreHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // 1. Initialize Views
        rvCart = findViewById(R.id.rvCartItems)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        tvEmptyCartMsg = findViewById(R.id.tvEmptyCartMsg) // Make sure this is in your XML

        setupRecyclerView()
        updateUI()

        // 2. Place Order Button Logic
        btnPlaceOrder.setOnClickListener {
            val items = CartManager.getCartList()
            if (items.isNotEmpty()) {
                placeFinalOrder(items)
            } else {
                Toast.makeText(this, "Add items to your cart first!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        rvCart.layoutManager = LinearLayoutManager(this)

        // 3. The "onCartChanged" Lambda logic
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