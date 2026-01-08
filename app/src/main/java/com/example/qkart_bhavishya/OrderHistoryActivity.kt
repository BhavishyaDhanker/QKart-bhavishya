package com.example.qkart_bhavishya

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var adapter: StudentOrderAdapter
    private val database by lazy { AppRoomDatabase.getDatabase(this) }
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        bottomNav = findViewById(R.id.bottomNavigationView)

        val rv = findViewById<RecyclerView>(R.id.rvOrderHistory)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = StudentOrderAdapter(emptyList())
        rv.adapter = adapter

        // Setup Bottom Navigation
        setupBottomNav()

        // Initial Badge Check
        updateCartBadge()

        observeOrders()
        syncFirestoreToRoom()
    }

    override fun onResume() {
        super.onResume()
        // Highlight the 'History' tab when this activity is visible
        bottomNav.selectedItemId = R.id.nav_history
        updateCartBadge()
    }

    private fun setupBottomNav() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainScreenActivity::class.java))
                    finish() // Close history so we don't stack
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, ActivityCart::class.java))
                    // Do not finish() if you want to allow back press to return here
                    true
                }
                R.id.nav_history -> {
                    // Already here
                    true
                }
                R.id.nav_account -> {
                    startActivity(Intent(this, AccountActivity::class.java))
                    finish()
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
            badge.backgroundColor = getColor(R.color.white) // Optional
            badge.badgeTextColor = getColor(R.color.maroon)
        } else {
            badge.isVisible = false
        }
    }

    private fun observeOrders() {
        lifecycleScope.launch {
            database.orderDao().getAllOrders().collect { entityList ->
                val modelList = entityList.map { entity ->
                    OrderModel(
                        orderId = entity.orderId,
                        rollNo = entity.rollNo,
                        status = entity.status,
                        timestamp = entity.timestamp,
                        totalAmount = entity.totalAmount,
                        // Passing combined string into itemName via a temporary CartItem
                        items = listOf(CartItem(
                            name = entity.itemName,
                            quantity = -1
                        ))
                    )
                }
                adapter.updateList(modelList)
            }
        }
    }

    private fun syncFirestoreToRoom() {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val myRollNo = prefs.getString("rollNo", "") ?: ""

        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        firestore.collection("orders")
            .whereEqualTo("rollNo", myRollNo)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val entities = querySnapshot.documents.mapNotNull { doc ->
                    val model = doc.toObject(OrderModel::class.java)
                    val combinedNames = model?.items?.joinToString { "${it.quantity}x ${it.name}" } ?: "Unknown Order"

                    OrderEntity(
                        orderId = doc.id,
                        itemName = combinedNames,
                        rollNo = doc.getString("rollNo") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        status = doc.getString("status") ?: "Pending",
                        totalAmount = doc.getDouble("totalAmount") ?: 0.0
                    )
                }

                lifecycleScope.launch {
                    database.orderDao().insertOrders(entities)
                }
            }
    }
}