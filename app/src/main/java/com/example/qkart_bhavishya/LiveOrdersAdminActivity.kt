package com.example.qkart_bhavishya

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LiveOrdersAdminActivity : AppCompatActivity() {

    private lateinit var helper: FirestoreHelper
    private lateinit var adapter: AdminOrderAdapter

    // State to track whether completed orders are shown
    private var showCompletedOrders = true

    // Store the full list from Firestore locally so we can filter immediately on toggle
    private var allFetchedOrders: List<OrderModel> = emptyList()

    private lateinit var btnToggleVisibility: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_orders_admin)

        helper = FirestoreHelper()
        btnToggleVisibility = findViewById(R.id.btnToggleVisibility)

        //  Load saved preference (keeps the eye state consistent even if app restarts)
        val sharedPref = getSharedPreferences("AdminPrefs", MODE_PRIVATE)
        showCompletedOrders = sharedPref.getBoolean("showCompleted", true)
        updateEyeIcon() // Set correct icon on launch

        //  Navigation
        val btnManageMenu = findViewById<TextView>(R.id.btnManageMenu)
        btnManageMenu.setOnClickListener {
            val intent = Intent(this, ManageMenuAdminActivity::class.java)
            startActivity(intent)
            finish()
        }

        //  Eye Button Click Logic
        btnToggleVisibility.setOnClickListener {
            // Toggle the state
            showCompletedOrders = !showCompletedOrders

            // Save the state
            sharedPref.edit().putBoolean("showCompleted", showCompletedOrders).apply()

            // Update UI
            updateEyeIcon()
            applyFilterAndSort()

            val message = if (showCompletedOrders) "Showing Completed Orders" else "Hidden Completed Orders"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        // Setup RecyclerView
        val rvOrders = findViewById<RecyclerView>(R.id.rvorders)
        rvOrders.layoutManager = LinearLayoutManager(this)
        adapter = AdminOrderAdapter(emptyList(), helper)
        rvOrders.adapter = adapter

        //  Real-time Listener
        helper.observeOrders { allOrders ->
            // Save raw data
            allFetchedOrders = allOrders
            // Update the UI based on current filter settings
            applyFilterAndSort()
        }
    }

    private fun updateEyeIcon() {
        if (showCompletedOrders) {
            // Open Eye Icon
            btnToggleVisibility.setImageResource(R.drawable.baseline_visibility_24)
        } else {
            // Slashed Eye Icon
            btnToggleVisibility.setImageResource(R.drawable.baseline_visibility_off_24)
        }
    }

    private fun applyFilterAndSort() {
        // Filter
        val filteredList = if (showCompletedOrders) {
            allFetchedOrders // Show everything
        } else {
            // Only show orders that are NOT completed
            allFetchedOrders.filter { it.status != "Completed" }
        }

       // sort the orders
        val sortedList = filteredList.sortedWith(compareBy<OrderModel> {
            it.status == "Completed" // Moves "Completed" to the bottom
        }.thenByDescending { it.timestamp }) // Newest first

        // Updates Adapter
        adapter.updateList(sortedList)
    }
}