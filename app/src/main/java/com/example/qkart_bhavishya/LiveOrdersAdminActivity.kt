package com.example.qkart_bhavishya

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
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

        // Navigation to Manage Menu
        val btnManageMenu = findViewById<TextView>(R.id.btnManageMenu)
        btnManageMenu.setOnClickListener {
            val intent = Intent(this, ManageMenuAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 1. Setup RecyclerView
        val rvOrders = findViewById<RecyclerView>(R.id.rvorders)
        rvOrders.layoutManager = LinearLayoutManager(this)

        // 2. Initialize Adapter (Passing helper for the button clicks)
        adapter = AdminOrderAdapter(emptyList(), helper)
        rvOrders.adapter = adapter

        // 3. Real-time Listener for Orders
        helper.observeOrders { allOrders ->
            // In the Live tab, we usually hide 'Completed' orders to keep it clean
            val liveOrders = allOrders.filter { it.status != "Completed" }
            adapter.updateList(liveOrders)
        }
    }
}