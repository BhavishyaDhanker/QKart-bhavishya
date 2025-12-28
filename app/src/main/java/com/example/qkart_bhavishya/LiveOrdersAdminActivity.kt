package com.example.qkart_bhavishya

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LiveOrdersAdminActivity : AppCompatActivity() {

    private lateinit var helper: FirestoreHelper
    private lateinit var adapter: AdminOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_orders_admin)

        helper = FirestoreHelper()

        val btnManageMenu = findViewById<TextView>(R.id.btnManageMenu)
        btnManageMenu.setOnClickListener {
            val intent = Intent(this, ManageMenuAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Inside LiveOrdersAdminActivity.kt

        val btnClear = findViewById<ImageView>(R.id.btnClearHistory)

        btnClear.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hide Completed Orders")
                .setMessage("Do you want to hide all current completed orders from this view? (They will still stay in the database)")
                .setPositiveButton("Hide") { _, _ ->
                    val sharedPref = getSharedPreferences("AdminPrefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putLong("hideOrdersBefore", System.currentTimeMillis())
                        apply()
                    }
                    // The listener will automatically pick this up or you can call your fetch again
                    refreshOrderList()
                    Toast.makeText(this, "History Hidden", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // 1. Setup RecyclerView
        val rvOrders = findViewById<RecyclerView>(R.id.rvorders)
        rvOrders.layoutManager = LinearLayoutManager(this)

        // 2. Initialize Adapter (Passing helper for the button clicks)
        adapter = AdminOrderAdapter(emptyList(), helper)
        rvOrders.adapter = adapter

        // 3. Real-time Listener for Orders
        helper.observeOrders { allOrders ->


            val sortedOrders = allOrders.sortedWith(compareBy<OrderModel> {
                it.status == "Completed" // Boolean false (0) comes before true (1)
            }.thenByDescending { it.timestamp })

            adapter.updateList(sortedOrders)
        }
    }

    private fun refreshOrderList() {
        helper.observeOrders { allOrders ->
            val sharedPref = getSharedPreferences("AdminPrefs", MODE_PRIVATE)
            val hideBefore = sharedPref.getLong("hideOrdersBefore", 0L)

            val filteredOrders = allOrders.filter { order ->
                if (order.status == "Completed") {
                    // Only show if it was completed AFTER the hide button was clicked
                    order.timestamp > hideBefore
                } else {
                    // Always show Pending, Preparing, and Ready orders
                    true
                }
            }.sortedWith(compareBy<OrderModel> {
                it.status == "Completed"
            }.thenByDescending { it.timestamp })

            adapter.updateList(filteredOrders)
        }
    }
}